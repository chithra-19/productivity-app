package com.climbup.controller.task;

import com.climbup.model.Activity;
import com.climbup.model.Activity.ActivityType;
import com.climbup.model.User;
import com.climbup.service.task.ActivityService;
import com.climbup.service.user.UserService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final UserService userService;

    @Autowired
    public ActivityController(ActivityService activityService, UserService userService) {
        this.activityService = activityService;
        this.userService = userService;
    }


    // ---------------- Get All Activities ----------------
    @GetMapping
    public ResponseEntity<List<Activity>> getAllActivities(
            @RequestParam(required = false) ActivityType type,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        User user = userService.getCurrentUser();
        LocalDateTime fromTs = (from != null) ? LocalDateTime.parse(from) : null;
        LocalDateTime toTs = (to != null) ? LocalDateTime.parse(to) : null;

        List<Activity> activities = activityService.getAllActivities(user, type, fromTs, toTs);
        return ResponseEntity.ok(activities);
    }

    // ---------------- Get Recent Activities (Paginated) ----------------
    @GetMapping("/recent")
    public ResponseEntity<Page<Activity>> getRecentActivities(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        User user = userService.getCurrentUser();
        Page<Activity> recentActivities = activityService.getRecentActivities(user, page, size);
        return ResponseEntity.ok(recentActivities);
    }
}
