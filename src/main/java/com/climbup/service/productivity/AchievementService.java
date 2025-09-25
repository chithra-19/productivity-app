package com.climbup.service.productivity;

import com.climbup.dto.request.AchievementRequestDTO;
import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.exception.NotFoundException;
import com.climbup.mapper.AchievementMapper;
import com.climbup.model.Achievement;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.AchievementRepository;
import com.climbup.repository.TaskRepository;
import com.climbup.service.task.TaskService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final TaskRepository taskRepository;
    private final TaskService taskService;

    public AchievementService(AchievementRepository achievementRepository,
                              TaskRepository taskRepository,
                              TaskService taskService) {
        this.achievementRepository = achievementRepository;
        this.taskRepository = taskRepository;
        this.taskService = taskService;
    }

    // ---------------- Create a new achievement ----------------
    public AchievementResponseDTO createAchievement(AchievementRequestDTO dto, User user) {
        Achievement achievement = AchievementMapper.toEntity(dto, user);
        Achievement saved = achievementRepository.save(achievement);
        return AchievementMapper.toResponseDTO(saved);
    }

    // ---------------- Get all achievements as DTOs ----------------
    public List<AchievementResponseDTO> getUserAchievements(User user) {
        return achievementRepository.findByUser(user).stream()
                .map(AchievementMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ---------------- Get all achievements as entities ----------------
    public List<Achievement> getUserAchievementsEntities(User user) {
        return achievementRepository.findByUser(user);
    }

    // ---------------- Unlock achievement manually ----------------
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
        }

        return AchievementMapper.toResponseDTO(achievement);
    }

    // ---------------- Check if user has newly unlocked achievements ----------------
    public boolean hasNewAchievement(User user) {
        return !achievementRepository.findByUserAndNewlyUnlockedTrue(user).isEmpty();
    }

    // ---------------- Mark newly unlocked achievements as seen ----------------
    @Transactional
    public void markAchievementsAsSeen(User user) {
        List<Achievement> newAchievements = achievementRepository.findByUserAndNewlyUnlockedTrue(user);
        newAchievements.forEach(a -> a.setNewlyUnlocked(false));
        achievementRepository.saveAll(newAchievements);
    }

    // ---------------- Automatically check and unlock achievements ----------------
    @Transactional
    public void checkForNewAchievements(User user) {
        List<Achievement> lockedAchievements = achievementRepository.findByUserAndUnlocked(user, false);

        for (Achievement achievement : lockedAchievements) {
            if (shouldUnlockAchievement(user, achievement)) {
                achievement.setUnlocked(true);
                achievement.setUnlockedDate(LocalDate.now());
                achievement.setNewlyUnlocked(true);
            }
        }

        if (!lockedAchievements.isEmpty()) {
            achievementRepository.saveAll(lockedAchievements);
        }
    }


    // ---------------- Rules to unlock achievements ----------------
    private boolean shouldUnlockAchievement(User user, Achievement achievement) {
        String title = achievement.getTitle();

        switch (title) {
            case "First Step":
                return taskService.getCompletedTaskCount(user) >= 1;
            case "Streak Starter":
                return getCurrentStreak(user) >= 3;
            case "Task Master":
                return taskService.getCompletedTaskCount(user) >= 10;
            case "Early Bird":
                return taskRepository.findByUserAndCompletedTrue(user).stream()
                        .anyMatch(task -> task.getCompletedDateTime() != null &&
                                task.getCompletedDateTime().getHour() < 8);
            case "Productivity Pro":
                return getProductivityScore(user) >= 80;
            default:
                return false;
        }
    }

    // ---------------- Calculate productivity score ----------------
    public int getProductivityScore(User user) {
        List<Task> allTasks = taskRepository.findByUser(user);
        if (allTasks.isEmpty()) return 0;

        long completedTasks = allTasks.stream().filter(Task::isCompleted).count();
        double score = ((double) completedTasks / allTasks.size()) * 100;
        return (int) Math.round(score);
    }

    // ---------------- Get current streak ----------------
    public int getCurrentStreak(User user) {
        List<Task> completedTasks = taskRepository.findByUserAndCompletedTrueOrderByCompletedDateTimeDesc(user);
        if (completedTasks.isEmpty()) return 0;

        int streak = 0;
        LocalDate current = LocalDate.now();

        for (Task task : completedTasks) {
            LocalDate completedDate = task.getCompletionDate();
            if (completedDate.equals(current)) {
                streak++;
                current = current.minusDays(1);
            } else if (completedDate.isBefore(current)) {
                break;
            }
        }
        return streak;
    }

    // ---------------- Initialize default achievements ----------------
    @Transactional
    public void initializeAchievements(User user) {
        if (achievementRepository.countByUser(user) == 0) {
            List<Achievement> defaultAchievements = List.of(
                    createDefaultAchievement("First Step", "Complete your first task", "bi-check-circle", user),
                    createDefaultAchievement("Streak Starter", "Complete tasks for 3 consecutive days", "bi-fire", user),
                    createDefaultAchievement("Task Master", "Complete 10 tasks", "bi-trophy", user),
                    createDefaultAchievement("Early Bird", "Complete a task before 8 AM", "bi-sun", user),
                    createDefaultAchievement("Productivity Pro", "Maintain 80%+ productivity for a week", "bi-graph-up", user)
            );
            achievementRepository.saveAll(defaultAchievements);
        }
    }

    private Achievement createDefaultAchievement(String title, String description, String icon, User user) {
        Achievement achievement = new Achievement();
        achievement.setTitle(title);
        achievement.setDescription(description);
        achievement.setIcon(icon);
        achievement.setUser(user);
        achievement.setUnlocked(false);
        achievement.setNewlyUnlocked(false);
        return achievement;
    }
}
