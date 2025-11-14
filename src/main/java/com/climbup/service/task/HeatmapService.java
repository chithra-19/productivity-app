package com.climbup.service.task;

import com.climbup.model.Activity;
import com.climbup.model.Activity.ActivityType;
import com.climbup.model.User;
import com.climbup.repository.ActivityRepository;
import com.climbup.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HeatmapService {

    private final ActivityRepository activityRepository;
    private final UserService userService;

    @Autowired
    public HeatmapService(ActivityRepository activityRepository, UserService userService) {
        this.activityRepository = activityRepository;
        this.userService = userService;
    }

    /**
     * Core heatmap logic including streaks and focus minutes.
     * Returns a map with streak metadata and daily activity breakdown.
     */
    public Map<String, Object> getHeatmapDataForUser(User user, ActivityType type, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = LocalDate.now().atTime(23, 59, 59);

        List<Activity> activities = activityRepository.findByUserAndTimestampBetween(user, startDateTime, endDateTime);

        if (type != null) {
            activities = activities.stream()
                    .filter(a -> a.getType() == type)
                    .collect(Collectors.toList());
        }

        Map<LocalDate, List<Activity>> groupedByDate = activities.stream()
                .collect(Collectors.groupingBy(a -> a.getTimestamp().toLocalDate()));

        List<Map<String, Object>> heatmapData = new ArrayList<>();
        List<String> activeDates = new ArrayList<>();
        LocalDate previousDay = null;
        int currentStreak = 0;
        int maxStreak = 0;

        for (LocalDate date : startDate.datesUntil(LocalDate.now().plusDays(1)).toList()) {
            List<Activity> dayActivities = groupedByDate.getOrDefault(date, Collections.emptyList());
            int taskCount = dayActivities.size();
            int focusMinutes = dayActivities.stream().mapToInt(Activity::getFocusMinutes).sum();
            boolean isStreakDay = taskCount > 0;

            if (isStreakDay) {
                activeDates.add(date.toString());
                currentStreak = (previousDay != null && previousDay.plusDays(1).equals(date)) ? currentStreak + 1 : 1;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                currentStreak = 0;
            }

            Map<String, Object> dayMap = new HashMap<>();
            dayMap.put("date", date.toString());
            dayMap.put("taskCount", taskCount);
            dayMap.put("focusMinutes", focusMinutes);
            dayMap.put("isStreakDay", isStreakDay);

            heatmapData.add(dayMap);
            previousDay = date;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("activeDates", activeDates);
        response.put("days", days);
        response.put("maxStreak", maxStreak);
        response.put("heatmapData", heatmapData);

        return response;
    }

    /**
     * Public method called by controller.
     * Wraps the response in a list for compatibility with frontend expectations.
     */
    public List<Map<String, Object>> getHeatmapData(Long userId, ActivityType type, int days) {
        User user = userService.getUserById(userId);
        return List.of(getHeatmapDataForUser(user, type, days));
    }
}