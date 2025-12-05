package com.climbup.service.task;

import com.climbup.dto.response.HeatmapDTO;
import com.climbup.dto.response.HeatmapResponse;
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
     * Simple level calculation: every 100 XP = +1 level.
     * Returns a map with level, nextLevelXP and progressPercent.
     */
    private Map<String, Object> computeLevelAndProgress(int xp) {
        int level = xp / 100;
        int prevLevelXP = level * 100;
        int nextLevelXP = (level + 1) * 100;
        double progress = 0.0;
        if (nextLevelXP - prevLevelXP > 0) {
            progress = ((double)(xp - prevLevelXP) / (nextLevelXP - prevLevelXP)) * 100.0;
        }
        Map<String, Object> out = new HashMap<>();
        out.put("level", level);
        out.put("nextLevelXP", nextLevelXP);
        out.put("progress", progress);
        return out;
    }

    /**
     * Build canonical HeatmapResponse for a userId, optional category, and days window.
     * Uses ActivityRepository.findByUserAndTimestampBetween(User, start, end)
     */
    public HeatmapResponse buildHeatmapResponse(Long userId, ActivityType type, int days) {
        User user = userService.getUserById(userId);

        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = LocalDate.now().atTime(23, 59, 59);

        List<Activity> activities = activityRepository.findByUserAndTimestampBetween(user, startDateTime, endDateTime);

        if (type != null) {
            activities = activities.stream()
                    .filter(a -> a.getType() == type)
                    .collect(Collectors.toList());
        }

        // Group by date
        Map<LocalDate, List<Activity>> grouped = activities.stream()
                .collect(Collectors.groupingBy(a -> a.getTimestamp().toLocalDate()));

        List<HeatmapDTO> heatmapData = new ArrayList<>(days);
        List<String> activeDates = new ArrayList<>();

        int currentStreak = 0;
        int maxStreak = 0;
        LocalDate prev = null;
        int totalXP = 0;

        for (LocalDate date = startDate; !date.isAfter(LocalDate.now()); date = date.plusDays(1)) {
            List<Activity> dayActs = grouped.getOrDefault(date, Collections.emptyList());
            int taskCount = dayActs.size();
            int focusMinutes = dayActs.stream().mapToInt(Activity::getFocusMinutes).sum();
            boolean isStreakDay = taskCount > 0;

            if (isStreakDay) {
                activeDates.add(date.toString());
                currentStreak = (prev != null && prev.plusDays(1).equals(date)) ? currentStreak + 1 : 1;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                currentStreak = 0;
            }

            // XP rules (same as your DTO logic)
            totalXP += taskCount * 10;               // 10 XP per task
            totalXP += focusMinutes / 5;            // 1 XP per 5 focus minutes
            if (isStreakDay) totalXP += 5;          // small daily streak bonus

            heatmapData.add(new HeatmapDTO(date.toString(), taskCount, focusMinutes, isStreakDay));
            prev = date;
        }

        int totalDaysActive = (int) heatmapData.stream().filter(d -> d.getTaskCount() > 0).count();

        Map<String, Object> levelInfo = computeLevelAndProgress(totalXP);
        int level = (int) levelInfo.get("level");
        int nextLevelXP = (int) levelInfo.get("nextLevelXP");
        double progress = (double) levelInfo.get("progress");

        return new HeatmapResponse(
                activeDates,
                totalDaysActive,
                currentStreak,
                maxStreak,
                totalXP,
                level,
                nextLevelXP,
                progress,
                heatmapData
        );
    }

    /**
     * Convenience: return list of HeatmapDTO days (useful for other consumers)
     */
    public List<HeatmapDTO> getHeatmapDTOs(Long userId, ActivityType type, int days) {
        HeatmapResponse resp = buildHeatmapResponse(userId, type, days);
        return resp.getHeatmapData();
    }

}
