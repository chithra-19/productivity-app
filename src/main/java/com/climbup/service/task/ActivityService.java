package com.climbup.service.task;

import com.climbup.model.Activity;
import com.climbup.model.User;
import com.climbup.model.Activity.ActivityType;
import com.climbup.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    // ---------------- Logging Activities ----------------

    /**
     * Log activity with automatic timestamp.
     */
    @Transactional
    public void log(String description, ActivityType type, User user) {
        Activity activity = new Activity(description, type, user);
        activityRepository.save(activity);
    }

    /**
     * Log activity with custom timestamp.
     */
    @Transactional
    public void log(String description, ActivityType type, User user, LocalDateTime timestamp) {
        Activity activity = new Activity(description, type, user, timestamp);
        activityRepository.save(activity);
    }

    // ---------------- Fetch Activities ----------------

    /**
     * Get all activities for a user, optionally filtered by type and date range.
     */
    public List<Activity> getAllActivities(User user, ActivityType type, LocalDateTime from, LocalDateTime to) {
        if (type != null && from != null && to != null) {
            return activityRepository.findByUserAndTypeAndTimestampBetween(user, type, from, to);
        } else if (type != null) {
            return activityRepository.findByUserAndType(user, type);
        } else if (from != null && to != null) {
            return activityRepository.findByUserAndTimestampBetween(user, from, to);
        } else {
            return activityRepository.findByUser(user);
        }
    }

    /**
     * Get recent activities (latest first) with optional pagination.
     */
    public Page<Activity> getRecentActivities(User user, int page, int size) {
        return activityRepository.findByUser(
                user,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"))
        );
    }
}
