package com.climbup.service.productivity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final AchievementTemplateRepository templateRepository;
    private final XPService xpService;

    public AchievementEvaluationService(UserAchievementRepository achievementRepository,
                                        GoalRepository goalRepository,
                                        TaskRepository taskRepository,
                                        StreakTrackerService streakTrackerService,
                                        AchievementTemplateRepository templateRepository,
                                        XPService xpService) {
        this.achievementRepository = achievementRepository;
        this.goalRepository = goalRepository;
        this.taskRepository = taskRepository;
        this.streakTrackerService = streakTrackerService;
        this.templateRepository = templateRepository;
        this.xpService = xpService;
    }

    @Transactional
    public void evaluate(User user) {

        // 🔹 Fetch user achievements
        List<UserAchievement> list = achievementRepository.findByUserId(user.getId());

        Map<AchievementCode, UserAchievement> map =
                list.stream().collect(Collectors.toMap(
                        ua -> ua.getTemplate().getCode(),
                        ua -> ua,
                        (a, b) -> a
                ));

        // 🔹 Metrics
        long goals = goalRepository.countByUserAndStatus(user, GoalStatus.COMPLETED);
        long tasks = taskRepository.countByUserAndCompletedTrue(user);
        long streak = streakTrackerService.getCurrentStreak(user);

        boolean early = taskRepository.existsEarlyMorningTask(user);
        boolean night = taskRepository.existsLateNightTask(user);

        int xp = xpService.calculateXpProgress(user);

        // 🔹 Evaluate dynamically
        List<AchievementTemplate> templates = templateRepository.findAll();

        for (AchievementTemplate template : templates) {

            UserAchievement ua = map.get(template.getCode());

            // 🔥 CREATE IF MISSING
            if (ua == null) {
                ua = new UserAchievement();
                ua.setUser(user);
                ua.setTemplate(template);
                ua.setUnlocked(false);

                achievementRepository.save(ua);
                map.put(template.getCode(), ua);
            }

            boolean condition = checkCondition(
                    template, goals, tasks, streak, xp, early, night
            );

            unlock(map, template.getCode(), condition);
        }
    }

    private void unlock(Map<AchievementCode, UserAchievement> map,
            AchievementCode code,
            boolean condition) {

if (!condition) return;

UserAchievement ua = map.get(code);

if (ua != null && !ua.isUnlocked()) {
ua.setUnlocked(true);
ua.setNewlyUnlocked(true);  // 🔥 Add this line
ua.setUnlockedAt(java.time.Instant.now());
achievementRepository.save(ua);
}
}


    private boolean checkCondition(AchievementTemplate template,
                                   long goals,
                                   long tasks,
                                   long streak,
                                   int xp,
                                   boolean early,
                                   boolean night) {

        String metric = template.getMetric();
        Integer threshold = template.getThreshold();

        if (metric == null) return false;

        return switch (metric) {

            case "GOALS" -> threshold != null && goals >= threshold;

            case "TASKS" -> threshold != null && tasks >= threshold;

            case "STREAK" -> threshold != null && streak >= threshold;

            case "XP" -> threshold != null && xp >= threshold;

            case "EARLY" -> early;

            case "NIGHT" -> night;

            default -> false;
        };
    }
}