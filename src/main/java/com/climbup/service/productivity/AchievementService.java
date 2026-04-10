package com.climbup.service.productivity;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.climbup.dto.request.AchievementRequestDTO;
import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.mapper.AchievementMapper;
import com.climbup.model.Achievement;
import com.climbup.model.Achievement.AchievementCode;
import com.climbup.model.ActivityType;
import com.climbup.model.Goal;
import com.climbup.model.GoalStatus;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.AchievementRepository;
import com.climbup.repository.GoalRepository;
import com.climbup.repository.TaskRepository;
import com.climbup.service.task.ActivityService;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final GoalRepository goalRepository;
    private final TaskRepository taskRepository;
    private final ActivityService activityService;

    public AchievementService(AchievementRepository achievementRepository,
                               GoalRepository goalRepository,
                               TaskRepository taskRepository,
                               ActivityService activityService) {
        this.achievementRepository = achievementRepository;
        this.goalRepository = goalRepository;
        this.taskRepository = taskRepository;
        this.activityService = activityService;
    }

    // ================= GET USER ACHIEVEMENTS =================
    public List<Achievement> getUserAchievements(User user) {
        return achievementRepository.findByUser(user);
    }

    // ================= SEED GOAL ACHIEVEMENT =================
    public void seedAchievementsForGoal(Goal goal) {

        boolean exists = achievementRepository.existsByUserAndGoal(goal.getUser(), goal);

        if (!exists) {
            Achievement a = new Achievement();
            a.setUser(goal.getUser());
            a.setTitle("Complete Goal: " + goal.getTitle());
            a.setDescription("Finish the goal: " + goal.getTitle());
            a.setType(Achievement.Type.GOAL);
            a.setIcon("bi-flag");
            a.setGoal(goal);

            achievementRepository.save(a);
        }
    }

    // ================= INITIAL SEEDING =================
    @Transactional
    public List<AchievementResponseDTO> initializeAchievements(User user) {

        if (achievementRepository.countByUser(user) > 0) {
            return achievementRepository.findByUser(user)
                    .stream()
                    .map(AchievementMapper::toResponseDTO)
                    .toList();
        }

        List<Achievement> seeded = List.of(
                create(user, AchievementCode.FIRST_STEP,
                        "First Step", "Complete your first task", Achievement.Type.TASK, "bi-check-circle"),

                create(user, AchievementCode.GOAL_1,
                        "Goal Setter", "Complete 1 goal", Achievement.Type.GOAL, "bi-flag"),

                create(user, AchievementCode.GOAL_5,
                        "Goal Grinder", "Complete 5 goals", Achievement.Type.GOAL, "bi-trophy"),

                create(user, AchievementCode.GOAL_10,
                        "Goal Master", "Complete 10 goals", Achievement.Type.GOAL, "bi-award"),

                create(user, AchievementCode.TASK_MASTER,
                        "Task Master", "Complete 10 tasks", Achievement.Type.TASK, "bi-lightning"),

                create(user, AchievementCode.EARLY_BIRD,
                        "Early Bird", "Complete a task before 8 AM", Achievement.Type.TASK, "bi-sun")
        );

        return achievementRepository.saveAll(seeded)
                .stream()
                .map(AchievementMapper::toResponseDTO)
                .toList();
    }

    private Achievement create(User user,
                               AchievementCode code,
                               String title,
                               String description,
                               Achievement.Type type,
                               String icon) {

        Achievement a = new Achievement();
        a.setUser(user);
        a.setCode(code);
        a.setTitle(title);
        a.setDescription(description);
        a.setType(type);
        a.setIcon(icon);
        return a;
    }

    // ================= EVALUATE ACHIEVEMENTS (FIXED) =================
    @Transactional
    public void evaluateAchievements(User user) {

        // 🔥 LOAD ONCE (FIXED N+1 PROBLEM)
        List<Achievement> achievements =
                achievementRepository.findByUser(user);

        Map<AchievementCode, Achievement> achievementMap =
                achievements.stream()
                        .filter(a -> a.getCode() != null)
                        .collect(Collectors.toMap(
                                Achievement::getCode,
                                a -> a,
                                (a1, a2) -> a1
                        ));

        long completedGoals =
                goalRepository.countByUserAndStatus(user, GoalStatus.COMPLETED);

        long completedTasks =
                taskRepository.countByUserAndCompletedTrue(user);

        unlockIfEligible(achievementMap, AchievementCode.GOAL_1, completedGoals >= 1);
        unlockIfEligible(achievementMap, AchievementCode.GOAL_5, completedGoals >= 5);
        unlockIfEligible(achievementMap, AchievementCode.GOAL_10, completedGoals >= 10);

        unlockIfEligible(achievementMap, AchievementCode.FIRST_STEP, completedTasks >= 1);
        unlockIfEligible(achievementMap, AchievementCode.TASK_MASTER, completedTasks >= 10);

        boolean earlyBird =
                taskRepository.findByUserAndCompletedTrue(user)
                        .stream()
                        .anyMatch(t ->
                                t.getCompletedDateTime() != null &&
                                t.getCompletedDateTime()
                                        .atZone(ZoneId.systemDefault())
                                        .getHour() < 8
                        );

        unlockIfEligible(achievementMap, AchievementCode.EARLY_BIRD, earlyBird);
    }

    // ================= FIXED UNLOCK =================
    private void unlockIfEligible(Map<AchievementCode, Achievement> map,
                                  AchievementCode code,
                                  boolean condition) {

        if (!condition) return;

        Achievement achievement = map.get(code);

        if (achievement != null && !achievement.isUnlocked()) {
            achievement.unlock();
            achievementRepository.save(achievement);
        }
    }

    // ================= MARK AS SEEN =================
    @Transactional
    public void markAchievementsAsSeen(User user) {

        List<Achievement> achievements =
                achievementRepository.findByUserAndNewlyUnlockedTrue(user);

        achievements.forEach(Achievement::markSeen);
        achievementRepository.saveAll(achievements);
    }

    // ================= CREATE CUSTOM ACHIEVEMENT =================
    @Transactional
    public AchievementResponseDTO createAchievement(AchievementRequestDTO dto, User user) {

        Achievement achievement = new Achievement();
        achievement.setTitle(dto.getTitle());
        achievement.setDescription(dto.getDescription());
        achievement.setType(dto.getType());
        achievement.setCategory(dto.getCategory());

        if (dto.getUnlockedDate() != null) {
            achievement.setUnlockedAt(
                    dto.getUnlockedDate()
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
            );
        }

        achievement.setUser(user);

        achievementRepository.save(achievement);

        return AchievementMapper.toResponseDTO(achievement);
    }

    // ================= CHECK NEW =================
    public boolean checkForNewAchievements(User user) {
        return !achievementRepository.findByUserAndNewlyUnlockedTrue(user).isEmpty();
    }

    // ================= MANUAL UNLOCK =================
    @Transactional
    public AchievementResponseDTO unlockAchievement(Long achievementId, User user) {

        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new RuntimeException(
                        "Achievement not found with ID: " + achievementId));

        if (!achievement.getUser().equals(user)) {
            throw new IllegalStateException("User does not own this achievement");
        }

        if (!achievement.isUnlocked()) {
            achievement.unlock();
            achievementRepository.save(achievement);
        }

        activityService.log(
                "🏆 Achievement unlocked: " + achievement.getTitle(),
                ActivityType.ACHIEVEMENT_UNLOCKED,
                user
        );

        return AchievementMapper.toResponseDTO(achievement);
    }
}