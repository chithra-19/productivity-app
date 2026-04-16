package com.climbup.controller.task;

import com.climbup.dto.response.ActivityDTO;
import com.climbup.mapper.ActivityMapper;
import com.climbup.model.Activity;
import com.climbup.model.ActivityType;
import com.climbup.model.User;
import com.climbup.service.task.ActivityService;
import com.climbup.service.user.UserService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<ActivityDTO>> getAllActivities(
            @RequestParam(required = false) ActivityType type,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        User user = userService.getCurrentUser();
        Instant fromTs = (from != null) ? Instant.parse(from) : null;
        Instant toTs = (to != null) ? Instant.parse(to) : null;
        List<ActivityDTO> activities = activityService.getAllActivities(user, type, fromTs, toTs);
        return ResponseEntity.ok(activities);
    }


    // ---------------- Get Recent Activities (Paginated) ----------------
    @GetMapping("/recent")
    public ResponseEntity<Page<ActivityDTO>> getRecentActivities(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        User user = userService.getCurrentUser();
        Page<ActivityDTO> recentActivities = activityService.getRecentActivities(user, page, size);
        return ResponseEntity.ok(recentActivities);
    }


    // ---------------- Get Last 15 Activities (Dashboard) ----------------
    @GetMapping("/latest")
    public ResponseEntity<List<ActivityDTO>> getLatestActivities() {
        User user = userService.getCurrentUser();
        List<ActivityDTO> recentDTOs = activityService.getRecentActivities(user); // already DTO
        return ResponseEntity.ok(recentDTOs);
    }
    
    

}
