package com.climbup.service.productivity;

import com.climbup.dto.request.AchievementRequestDTO;
import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.exception.NotFoundException;
import com.climbup.mapper.AchievementMapper;
import com.climbup.model.Achievement;
import com.climbup.model.Achievement.AchievementCode;
import com.climbup.model.Goal;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.AchievementRepository;
import com.climbup.repository.GoalRepository;
import com.climbup.repository.TaskRepository;
import com.climbup.service.task.TaskService;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final TaskRepository taskRepository;
    private final GoalRepository goalRepository;
    private final TaskService taskService;

    public AchievementService(AchievementRepository achievementRepository,
                              TaskRepository taskRepository,
                              GoalRepository goalRepository,
                              @Lazy TaskService taskService) {
        this.achievementRepository = achievementRepository;
        this.taskRepository = taskRepository;
        this.goalRepository = goalRepository;
        this.taskService = taskService;
    }

    // ---------------- Create Achievement ----------------
    @Transactional
    public AchievementResponseDTO createAchievement(AchievementRequestDTO dto, User user) {
        Achievement achievement = AchievementMapper.toEntity(dto, user);
        Achievement saved = achievementRepository.save(achievement);
        return AchievementMapper.toResponseDTO(saved);
    }

    // ---------------- Get All User Achievements ----------------
    public List<AchievementResponseDTO> getUserAchievements(User user) {
        return achievementRepository.findByUser(user)
                .stream()
                .map(a -> AchievementMapper.toResponseDTO(a, a.isNewlyUnlocked()))
                .collect(Collectors.toList());
    }

    public List<Achievement> getUserAchievementsEntities(User user) {
        return achievementRepository.findByUser(user);
    }

    // ---------------- Unlock Manually ----------------
    @Transactional
    public AchievementResponseDTO unlockAchievement(Long achievementId, User user) {
        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new NotFoundException("Achievement not found"));

        if (!achievement.getUser().equals(user)) {
            throw new SecurityException("Achievement does not belong to user");
        }

        if (!achievement.isUnlocked()) {
            achievement.setUnlocked(true);
            achievement.setUnlockedDate(LocalDate.now());
            achievement.setNewlyUnlocked(true);
            achievementRepository.save(achievement);
        }

        return AchievementMapper.toResponseDTO(achievement, achievement.isNewlyUnlocked());
    }

    // ---------------- Newly Unlocked Check ----------------
    public boolean hasNewAchievement(User user) {
        return !achievementRepository.findByUserAndNewlyUnlockedTrue(user).isEmpty();
    }

    // ---------------- Mark as Seen ----------------
    @Transactional
    public void markAchievementsAsSeen(User user) {
        List<Achievement> newAchievements = achievementRepository.findByUserAndNewlyUnlockedTrue(user);
        newAchievements.forEach(a -> a.setNewlyUnlocked(false));
        achievementRepository.saveAll(newAchievements);
    }

    // ---------------- Auto Unlock Check ----------------
    @Transactional
    public void checkForNewAchievements(User user) {
        List<Achievement> lockedAchievements = achievementRepository.findByUserAndUnlocked(user, false);

        boolean anyUnlocked = false;

        for (Achievement achievement : lockedAchievements) {

            AchievementCode code = achievement.getCode(); // <-- FIXED

            if (shouldUnlock(user, code)) {
                achievement.setUnlocked(true);
                achievement.setUnlockedDate(LocalDate.now());
                achievement.setNewlyUnlocked(true);
                anyUnlocked = true;
            }
        }

        if (anyUnlocked) {
            achievementRepository.saveAll(lockedAchievements);
        }
    }

    // ---------------- Rules for Unlocking ----------------
    private boolean shouldUnlock(User user, AchievementCode code) {
        return switch (code) {
            case GOAL_1 -> goalRepository.findByUser(user).stream()
                    .filter(Goal::isCompleted)
                    .count() >= 1;

            case GOAL_5 -> goalRepository.findByUser(user).stream()
                    .filter(Goal::isCompleted)
                    .count() >= 5;

            case GOAL_10 -> goalRepository.findByUser(user).stream()
                    .filter(Goal::isCompleted)
                    .count() >= 10;

            case BEFORE_DEADLINE -> goalRepository.findByUser(user).stream()
                    .anyMatch(g -> g.isCompleted()
                            && g.getDueDate() != null
                            && g.getCompletedDate() != null
                            && !g.getCompletedDate().isAfter(g.getDueDate()));

            case STREAK_3 -> calculateGoalStreak(user) >= 3;
            case STREAK_7 -> calculateGoalStreak(user) >= 7;

            case HEATMAP_50 -> goalRepository.findByUser(user).stream()
                    .filter(Goal::isCompleted)
                    .map(Goal::getCompletedDate)
                    .distinct()
                    .count() >= 50;

            case FIRST_STEP -> taskService.getCompletedTaskCount(user) >= 1;
            case STREAK_STARTER -> getCurrentStreak(user) >= 3;
            case TASK_MASTER -> taskService.getCompletedTaskCount(user) >= 10;

            case EARLY_BIRD -> taskRepository.findByUserAndCompletedTrue(user).stream()
                    .anyMatch(task -> task.getCompletedDateTime() != null
                            && task.getCompletedDateTime().getHour() < 8);

            case PRODUCTIVITY_PRO -> getProductivityScore(user) >= 80;
            case GOAL_COMPLETED -> goalRepository.findByUser(user).stream()
                    .anyMatch(Goal::isCompleted);

            default -> false;
        };
    }

    // ---------------- Streak Calculations ----------------
    private int calculateGoalStreak(User user) {
        List<Goal> goals = goalRepository.findByUser(user);
        AtomicInteger streak = new AtomicInteger(0);
        LocalDate today = LocalDate.now();

        goals.stream()
                .filter(Goal::isCompleted)
                .map(Goal::getCompletedDate)
                .sorted((d1, d2) -> d2.compareTo(d1))
                .forEach(date -> {
                    if (streak.get() == 0 || date.plusDays(streak.get()).equals(today)) {
                        streak.incrementAndGet();
                    }
                });

        return streak.get();
    }

    public int getCurrentStreak(User user) {
        List<Task> completedTasks =
                taskRepository.findByUserAndCompletedTrueOrderByCompletedDateTimeDesc(user);

        if (completedTasks.isEmpty()) return 0;

        int streak = 0;
        LocalDate current = LocalDate.now();

        for (Task task : completedTasks) {
            LocalDate completedDate = task.getCompletedDateTime().toLocalDate();
            if (completedDate.equals(current)) {
                streak++;
                current = current.minusDays(1);
            } else if (completedDate.isBefore(current)) {
                break;
            }
        }

        return streak;
    }

    public int getProductivityScore(User user) {
        List<Task> tasks = taskRepository.findByUser(user);
        if (tasks.isEmpty()) return 0;

        long completed = tasks.stream().filter(Task::isCompleted).count();
        return (int) Math.round(((double) completed / tasks.size()) * 100);
    }

    // ---------------- Default Seeder ----------------
    @Transactional
    public void initializeAchievements(User user) {

        if (achievementRepository.countByUser(user) == 0) {

            List<Achievement> defaultAchievements = List.of(
                    createDefaultAchievement("First Step", "Complete your first task", "bi-check-circle", user, AchievementCode.FIRST_STEP),
                    createDefaultAchievement("Streak Starter", "Complete tasks for 3 consecutive days", "bi-fire", user, AchievementCode.STREAK_STARTER),
                    createDefaultAchievement("Task Master", "Complete 10 tasks", "bi-trophy", user, AchievementCode.TASK_MASTER),
                    createDefaultAchievement("Early Bird", "Complete a task before 8 AM", "bi-sun", user, AchievementCode.EARLY_BIRD),
                    createDefaultAchievement("Productivity Pro", "Maintain 80%+ productivity for a week", "bi-graph-up", user, AchievementCode.PRODUCTIVITY_PRO),
                    createDefaultAchievement("Goal Completed", "Complete any goal", "bi-flag", user, AchievementCode.GOAL_COMPLETED)
            );

            achievementRepository.saveAll(defaultAchievements);
        }
    }

    private Achievement createDefaultAchievement(String title, String description, String icon, User user, AchievementCode code) {
        Achievement achievement = new Achievement();
        achievement.setTitle(title);
        achievement.setDescription(description);
        achievement.setIcon(icon);
        achievement.setUser(user);
        achievement.setUnlocked(false);
        achievement.setNewlyUnlocked(false);
        achievement.setCode(code); // <-- FIXED
        return achievement;
    }

    // ---------------- Refresh Achievements ----------------
    @Transactional
    public List<AchievementResponseDTO> refreshAchievements(User user) {
        checkForNewAchievements(user);
        return getUserAchievements(user);
    }

    // ---------------- Lock Manually ----------------
    @Transactional
    public void lockAchievement(User user, Long achievementId) {
        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new NotFoundException("Achievement not found"));

        if (!achievement.getUser().equals(user)) {
            throw new SecurityException("Achievement does not belong to user");
        }

        achievement.setUnlocked(false);
        achievementRepository.save(achievement);
    }
    
    public void unlockGoalAchievement(Goal goal, AchievementCode code) {
        Optional<Achievement> existing = achievementRepository
            .findByUserIdAndCode(goal.getUser().getId(), code);

        Achievement achievement = existing.orElseGet(() -> {
            Achievement a = new Achievement();
            a.setUser(goal.getUser());
            a.setCode(code);
            a.setGoal(goal);

            // Always use code title for consistency with dashboard
            a.setTitle(goal.getTitle());
            a.setDescription("You completed the goal: " + goal.getTitle());
            return a;
        });

        achievement.setGoal(goal);
        achievement.unlock();
        achievementRepository.save(achievement);
    }



}
