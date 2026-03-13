package com.climbup.service.productivity;

import com.climbup.model.ActivityLog;
import com.climbup.model.ActivityType;
import com.climbup.model.User;
import com.climbup.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    // ------------------------------------------------
    // GENERIC ACTIVITY LOGGER
    // ------------------------------------------------
    public void logActivity(User user,
                            ActivityType type,
                            String description,
                            Integer taskCount,
                            Integer focusMinutes,
                            String category) {

        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setType(type);
        log.setDescription(description);
        log.setActivityDate(LocalDate.now());

        log.setTaskCount(taskCount != null ? taskCount : 0);
        log.setFocusMinutes(focusMinutes != null ? focusMinutes : 0);
        log.setCategory(category);

        activityLogRepository.save(log);
    }

    // ------------------------------------------------
    // SIMPLE LOGGER (MOST COMMON USE)
    // ------------------------------------------------
    public void logSimpleActivity(User user,
                                  ActivityType type,
                                  String description) {

        logActivity(user, type, description, 0, 0, null);
    }

    // ------------------------------------------------
    // GET RECENT ACTIVITY (PROFILE PAGE)
    // ------------------------------------------------
    public List<ActivityLog> getRecentActivities(User user) {
        return activityLogRepository
                .findTop10ByUserOrderByLoggedAtDesc(user);
    }

    // ------------------------------------------------
    // GET ACTIVITY BETWEEN DATES (REPORT FEATURE)
    // ------------------------------------------------
    public List<ActivityLog> getActivitiesBetween(User user,
                                                  LocalDate startDate,
                                                  LocalDate endDate) {

        return activityLogRepository
                .findByUserAndActivityDateBetweenOrderByActivityDateDesc(
                        user,
                        startDate,
                        endDate
                );
    }
}
