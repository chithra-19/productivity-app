package com.climbup.service.task;

import com.climbup.model.User;
import com.climbup.model.Activity;
import com.climbup.model.ActivityLog;
import com.climbup.dto.response.HeatmapResponse;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.productivity.ActivityLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ActivityLogService activityLogService;
    private final AchievementService achievementService;
    private final HeatmapService heatmapService;

    public DashboardService(ActivityLogService activityLogService,
                            AchievementService achievementService,
                            HeatmapService heatmapService) {
        this.activityLogService = activityLogService;
        this.achievementService = achievementService;
        this.heatmapService = heatmapService;
    }

    public HeatmapResponse getUserHeatmap(User user, String category, LocalDate from, LocalDate to) {
        Activity.ActivityType type = null;
        try {
            type = Activity.ActivityType.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            // fallback: treat unknown category as null (all types)
        }

        int days = (int) (to.toEpochDay() - from.toEpochDay()) + 1;

        return heatmapService.buildHeatmapResponse(user.getId(), type, days);
    }

    public Object getUserAchievements(User user) {
        return achievementService.getUserAchievements(user);
    }
}