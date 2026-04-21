package com.climbup.config;

import com.climbup.model.*;
import com.climbup.repository.AchievementTemplateRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;

import org.springframework.stereotype.Component;

@Component
public class AchievementTemplateSeeder {

    private final AchievementTemplateRepository repo;

    public AchievementTemplateSeeder(AchievementTemplateRepository repo) {
        this.repo = repo;
    }
   

    @PostConstruct
    public void seed() {

        for (AchievementCode code : AchievementCode.values()) {

        	if (!repo.existsByCode(code)) {

        	    AchievementTemplate t = new AchievementTemplate();

        	    t.setCode(code);
        	    t.setTitle(generateTitle(code));
        	    t.setDescription(generateDescription(code));
        	    t.setType(getType(code));

        	    setRules(t, code);

        	    repo.save(t);
        	}
        }
    }

    private String generateTitle(AchievementCode code) {
        return switch (code) {
        
        	case CUSTOM_GOAL -> "Custom Goal";
            // Goals
            case GOAL_1_COMPLETED -> "First Goal Completed";
            case GOAL_5_COMPLETED -> "5 Goals Completed";
            case GOAL_10_COMPLETED -> "10 Goals Completed";
            case GOAL_25_COMPLETED -> "25 Goals Completed";

            // Tasks
            case TASK_25_COMPLETED -> "25 Tasks Completed";
            case TASK_50_COMPLETED -> "50 Tasks Completed";
            case TASK_100_COMPLETED -> "100 Tasks Completed";
            case TASK_MASTER -> "Task Master";

            // Streak
            case STREAK_7_DAYS -> "7-Day Streak";
            case STREAK_21_DAYS -> "21-Day Streak";
            case STREAK_50_DAYS -> "50-Day Streak";
            case STREAK_100_DAYS -> "100-Day Streak";

            // Time
            case EARLY_BIRD -> "Early Bird";
            case NIGHT_OWL -> "Night Owl";

            // Productivity
            case PRODUCTIVITY_BRONZE -> "Productivity Bronze";
            case PRODUCTIVITY_SILVER -> "Productivity Silver";
            case PRODUCTIVITY_GOLD -> "Productivity Gold";
            case PRODUCTIVITY_LEGEND -> "Productivity Legend";
        };
    }

    private String generateDescription(AchievementCode code) {
        return switch (code) {
        
        	case CUSTOM_GOAL -> "Complete a custom goal.";
        	
            // Goals
            case GOAL_1_COMPLETED -> "Complete your first goal.";
            case GOAL_5_COMPLETED -> "Complete 5 goals.";
            case GOAL_10_COMPLETED -> "Complete 10 goals.";
            case GOAL_25_COMPLETED -> "Complete 25 goals.";

            // Tasks
            case TASK_25_COMPLETED -> "Complete 25 tasks.";
            case TASK_50_COMPLETED -> "Complete 50 tasks.";
            case TASK_100_COMPLETED -> "Complete 100 tasks.";
            case TASK_MASTER -> "Become a task master.";

            // Streak
            case STREAK_7_DAYS -> "Maintain a 7-day streak.";
            case STREAK_21_DAYS -> "Maintain a 21-day streak.";
            case STREAK_50_DAYS -> "Maintain a 50-day streak.";
            case STREAK_100_DAYS -> "Maintain a 100-day streak.";

            // Time
            case EARLY_BIRD -> "Complete a task before 8 AM.";
            case NIGHT_OWL -> "Complete a task after 10 PM.";

            // Productivity
            case PRODUCTIVITY_BRONZE -> "Reach bronze productivity level.";
            case PRODUCTIVITY_SILVER -> "Reach silver productivity level.";
            case PRODUCTIVITY_GOLD -> "Reach gold productivity level.";
            case PRODUCTIVITY_LEGEND -> "Reach legend productivity level.";
        };
    }

    private AchievementType getType(AchievementCode code) {
    return switch (code) {

    	case CUSTOM_GOAL -> AchievementType.GOAL;
        // Goals
        case GOAL_1_COMPLETED,
             GOAL_5_COMPLETED,
             GOAL_10_COMPLETED,
             GOAL_25_COMPLETED -> AchievementType.GOAL;

        // Tasks
        case TASK_25_COMPLETED,
             TASK_50_COMPLETED,
             TASK_100_COMPLETED,
             TASK_MASTER -> AchievementType.TASK;

        // Streak
        case STREAK_7_DAYS,
             STREAK_21_DAYS,
             STREAK_50_DAYS,
             STREAK_100_DAYS -> AchievementType.STREAK;

        // Time
        case EARLY_BIRD,
             NIGHT_OWL -> AchievementType.TIME;

        // Productivity
        case PRODUCTIVITY_BRONZE,
             PRODUCTIVITY_SILVER,
             PRODUCTIVITY_GOLD,
             PRODUCTIVITY_LEGEND -> AchievementType.PERFORMANCE;
    };
}
    private void setRules(AchievementTemplate t, AchievementCode code) {

        switch (code) {

        	case CUSTOM_GOAL -> { t.setMetric("CUSTOM"); }
            // GOALS
            case GOAL_1_COMPLETED -> { t.setMetric("GOALS"); t.setThreshold(1); }
            case GOAL_5_COMPLETED -> { t.setMetric("GOALS"); t.setThreshold(5); }
            case GOAL_10_COMPLETED -> { t.setMetric("GOALS"); t.setThreshold(10); }
            case GOAL_25_COMPLETED -> { t.setMetric("GOALS"); t.setThreshold(25); }

            // TASKS
            case TASK_25_COMPLETED -> { t.setMetric("TASKS"); t.setThreshold(25); }
            case TASK_50_COMPLETED -> { t.setMetric("TASKS"); t.setThreshold(50); }
            case TASK_100_COMPLETED -> { t.setMetric("TASKS"); t.setThreshold(100); }
            case TASK_MASTER -> { t.setMetric("TASKS"); t.setThreshold(200); }

            // STREAK (your rule = ≥4 tasks/day handled in streak service)
            case STREAK_7_DAYS -> { t.setMetric("STREAK"); t.setThreshold(7); }
            case STREAK_21_DAYS -> { t.setMetric("STREAK"); t.setThreshold(21); }
            case STREAK_50_DAYS -> { t.setMetric("STREAK"); t.setThreshold(50); }
            case STREAK_100_DAYS -> { t.setMetric("STREAK"); t.setThreshold(100); }

            // TIME
            case EARLY_BIRD -> { t.setMetric("EARLY"); }
            case NIGHT_OWL -> { t.setMetric("NIGHT"); }

            // PRODUCTIVITY (XP based)
            case PRODUCTIVITY_BRONZE -> { t.setMetric("XP"); t.setThreshold(50); }
            case PRODUCTIVITY_SILVER -> { t.setMetric("XP"); t.setThreshold(150); }
            case PRODUCTIVITY_GOLD -> { t.setMetric("XP"); t.setThreshold(300); }
            case PRODUCTIVITY_LEGEND -> { t.setMetric("XP"); t.setThreshold(600); }
        }
    }
}