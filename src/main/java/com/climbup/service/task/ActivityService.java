package com.climbup.service.task;

import com.climbup.dto.response.ActivityDTO;
import com.climbup.mapper.ActivityMapper;
import com.climbup.model.Activity;
import com.climbup.model.User;
import com.climbup.model.Task;
import com.climbup.model.ActivityType;
import com.climbup.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    // ---------------- Logging Activities ----------------

    @Transactional
    public void log(String description, ActivityType type, User user) {

        if (user == null || type == null || description == null) return;

        Activity activity = new Activity();
        activity.setDescription(description);
        activity.setType(type);
        activity.setUser(user);
        activity.setTimestamp(Instant.now());

        activityRepository.save(activity);
    }
    @Transactional
    public void logTaskCompleted(Task task, User user) {
        String msg = "Completed Task: " + task.getTitle();
        log(msg, ActivityType.TASK_COMPLETED, user);
    }

    // ---------------- Fetch Activities ----------------

    public List<ActivityDTO> getAllActivities(User user, ActivityType type, Instant fromTs, Instant toTs) {
        List<Activity> activities;

        if (type != null && fromTs != null && toTs != null) {
            activities = activityRepository.findByUserAndTypeAndTimestampBetween(user, type, fromTs, toTs);
        } else if (type != null) {
            activities = activityRepository.findByUserAndType(user, type);
        } else if (fromTs != null && toTs != null) {
            activities = activityRepository.findByUserAndTimestampBetween(user, fromTs, toTs);
        } else {
            activities = activityRepository.findByUser(user);
        }

        return activities.stream()
                .map(ActivityMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Page<ActivityDTO> getRecentActivities(User user, int page, int size) {
        return activityRepository.findByUser(
                        user,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"))
                )
                .map(ActivityMapper::toDTO);
    }

    public List<ActivityDTO> getRecentActivities(User user) {
        return activityRepository.findTop5ByUserOrderByTimestampDesc(user)
                .stream()
                .map(ActivityMapper::toDTO)
                .collect(Collectors.toList());
    }
}
