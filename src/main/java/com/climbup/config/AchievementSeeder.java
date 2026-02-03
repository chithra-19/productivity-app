package com.climbup.config;

import com.climbup.model.Achievement;
import com.climbup.model.Achievement.AchievementCode;
import com.climbup.repository.AchievementRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AchievementSeeder {

    private final AchievementRepository achievementRepository;

    @Autowired
    public AchievementSeeder(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    @PostConstruct
    public void seedAchievements() {

        for (AchievementCode code : AchievementCode.values()) {

            if (!achievementRepository.existsByCode(code)) {

                Achievement a = new Achievement();
                a.setCode(code);
                a.setTitle(generateTitle(code));
                a.setDescription(generateDescription(code));
                a.setUnlocked(false);   // seeders never unlock
                a.setUser(null);        // no user at seeding

                achievementRepository.save(a);
            }
        }
    }

    private String generateTitle(AchievementCode code) {
        return switch (code) {
            case GOAL_1 -> "First Goal Completed";
            case GOAL_5 -> "5 Goals Completed";
            case GOAL_10 -> "10 Goals Completed";
            case STREAK_3 -> "3-Day Streak";
            case STREAK_7 -> "7-Day Streak";
            case FIRST_STEP -> "First Task Completed";
            case TASK_MASTER -> "10 Tasks Completed";
            case EARLY_BIRD -> "Early Bird";
            case PRODUCTIVITY_PRO -> "Productivity Pro";
            case GOAL_COMPLETED -> "Goal Completed";
        };
    }

    private String generateDescription(AchievementCode code) {
        return switch (code) {
            case GOAL_1 -> "Complete your first goal.";
            case GOAL_5 -> "Complete 5 goals.";
            case GOAL_10 -> "Complete 10 goals.";
            case STREAK_3 -> "Maintain a 3-day streak.";
            case STREAK_7 -> "Maintain a 7-day streak.";
            case FIRST_STEP -> "Complete your first task.";
            case TASK_MASTER -> "Complete 10 tasks.";
            case EARLY_BIRD -> "Complete a task before 8 AM.";
            case PRODUCTIVITY_PRO -> "Reach a productivity score of 80.";
            case GOAL_COMPLETED -> "Complete any goal.";
        };
    }
}
