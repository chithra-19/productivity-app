package com.climbup.service.productivity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.climbup.model.*;
import com.climbup.repository.*;

@Service
public class AchievementEvaluationService {

    private final UserAchievementRepository achievementRepository;
    private final GoalRepository goalRepository;
    private final TaskRepository taskRepository;
    private final StreakTrackerService streakTrackerService;
    private final XPService xpService;
    private final AchievementTemplateRepository achievementTemplateRepository;

    public AchievementEvaluationService(UserAchievementRepository achievementRepository,
                                        GoalRepository goalRepository,
                                        TaskRepository taskRepository,
                                        StreakTrackerService streakTrackerService,
                                        XPService xpService,
                                        AchievementTemplateRepository achievementTemplateRepository) {

        this.achievementRepository = achievementRepository;
        this.goalRepository = goalRepository;
        this.taskRepository = taskRepository;
        this.streakTrackerService = streakTrackerService;
        this.xpService = xpService;
        this.achievementTemplateRepository = achievementTemplateRepository;
    }

    @Transactional
    public void evaluate(User user) {

        List<UserAchievement> existing =
                achievementRepository.findByUserId(user.getId());

        Map<Long, UserAchievement> templateMap =
                existing.stream()
                        .filter(ua -> ua.getTemplate() != null)
                        .collect(Collectors.toMap(
                                ua -> ua.getTemplate().getId(),
                                ua -> ua,
                                (a, b) -> a
                        ));

        long goals = goalRepository.countByUserAndStatus(user, GoalStatus.COMPLETED);
        long tasks = taskRepository.countByUserAndCompletedTrue(user);
        long streak = streakTrackerService.getCurrentStreak(user);
        int xp = xpService.calculateXpProgress(user);

        List<AchievementTemplate> templates = achievementTemplateRepository.findAll();

        List<UserAchievement> toSave = new ArrayList<>();

        // 🔹 TEMPLATE ACHIEVEMENTS
        for (AchievementTemplate template : templates) {

            UserAchievement ua = templateMap.get(template.getId());
            boolean isNew = false;

            if (ua == null) {
                ua = new UserAchievement();
                ua.setUser(user);
                ua.setTemplate(template);
                ua.setUnlocked(false);
                ua.setNewlyUnlocked(false);
                ua.setSeen(false);
                isNew = true;
            }

            boolean condition = checkCondition(template, goals, tasks, streak, xp);

            if (condition && !ua.isUnlocked()) {
                ua.unlock();
            }

            // ✅ IMPORTANT: save if new OR changed
            if (isNew || ua.isNewlyUnlocked()) {
                toSave.add(ua);
            }
        }

        // 🔹 GOAL ACHIEVEMENTS
        List<UserAchievement> goalAchievements =
                achievementRepository.findByUserAndGoalIsNotNull(user);

        for (UserAchievement ua : goalAchievements) {

            if (ua.isUnlocked()) continue;

            Goal goal = ua.getGoal();

            if (goal != null && goal.getStatus() == GoalStatus.COMPLETED) {
                ua.unlock();
                toSave.add(ua); // ✅ IMPORTANT
            }
        }

        // 🔥 SAVE ONLY CHANGED
        if (!toSave.isEmpty()) {
            achievementRepository.saveAll(toSave);
        }
    }
    private boolean checkCondition(AchievementTemplate template,
                                   long goals,
                                   long tasks,
                                   long streak,
                                   int xp) {

        String metric = template.getMetric();
        Integer threshold = template.getThreshold();

        if (metric == null || threshold == null) return false;

        return switch (metric.trim().toUpperCase()) {
            case "GOALS" -> goals >= threshold;
            case "TASKS" -> tasks >= threshold;
            case "STREAK" -> streak >= threshold;
            case "XP" -> xp >= threshold;
            default -> false;
        };
    }
}
