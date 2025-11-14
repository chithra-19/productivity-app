package com.climbup.service.productivity;

import com.climbup.dto.response.HeatmapDTO;
import com.climbup.model.ActivityLog;
import com.climbup.model.User;
import com.climbup.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository repository;

    // Log activity
    public void logActivity(User user, String category, LocalDate date, int focusMinutes) {
        ActivityLog log = repository.findByUserAndCategoryAndActivityDate(user, category, date)
                .orElseGet(() -> {
                    ActivityLog newLog = new ActivityLog();
                    newLog.setUser(user);
                    newLog.setCategory(category);
                    newLog.setActivityDate(date);
                    newLog.setTaskCount(0);
                    newLog.setFocusMinutes(0);
                    return newLog;
                });

        log.setTaskCount(log.getTaskCount() + 1);
        log.setFocusMinutes(log.getFocusMinutes() + focusMinutes);
        repository.save(log);
    }

    // Get current streak
    public int getCurrentStreak(User user, String category) {
        List<LocalDate> dates = getLogs(user, category, LocalDate.now().minusDays(30), LocalDate.now())
                .stream()
                .map(ActivityLog::getActivityDate)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        int streak = 0;
        LocalDate today = LocalDate.now();
        for (LocalDate date : dates) {
            if (date.equals(today.minusDays(streak))) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }

    // Fetch logs between dates
    public List<ActivityLog> getLogs(User user, String category, LocalDate from, LocalDate to) {
        return repository.findByUserAndCategoryAndActivityDateBetween(user, category, from, to);
    }

    // 🔹 Original method with category
    public List<HeatmapDTO> getHeatmapData(User user, String category) {
        return repository.findByUserAndCategoryAndActivityDateBetween(user, category, LocalDate.now().minusYears(1), LocalDate.now())
                .stream()
                .map(log -> new HeatmapDTO(
                        log.getActivityDate().toString(),
                        log.getTaskCount(),
                        log.getFocusMinutes(),
                        false // or implement streak logic per day if needed
                ))
                .collect(Collectors.toList());
    }

    // 🔹 Overloaded method without category (for backward compatibility)
    public List<HeatmapDTO> getHeatmapData(User user) {
        return getHeatmapData(user, "all"); // default category
    }
}
