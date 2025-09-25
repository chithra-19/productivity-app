package com.climbup.service.task;

import com.climbup.model.User;
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

    public DashboardService(ActivityLogService activityLogService, AchievementService achievementService) {
        this.activityLogService = activityLogService;
        this.achievementService = achievementService;
    }

    public HeatmapResponse getUserHeatmap(User user, String category, LocalDate from, LocalDate to) {
        List<ActivityLog> logs = activityLogService.getLogs(user, category, from, to);

        List<String> activeDates = logs.stream()
                .map(ActivityLog::getActivityDate)
                .map(LocalDate::toString)
                .distinct()
                .collect(Collectors.toList());

        int currentStreak = activityLogService.getCurrentStreak(user, category);

        HeatmapResponse response = new HeatmapResponse();
        response.setActiveDates(activeDates);
        response.setTotalDays(activeDates.size());
        response.setCurrentStreak(currentStreak);

        return response;
    }

    public Object getUserAchievements(User user) {
        return achievementService.getUserAchievements(user);
    }
}
