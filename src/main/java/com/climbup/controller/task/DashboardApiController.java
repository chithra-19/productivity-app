package com.climbup.controller.task;

import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.model.User;
import com.climbup.service.task.TaskQueryService;
import com.climbup.service.task.TaskStatsService;
import com.climbup.service.user.UserService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard/tasks")
public class DashboardApiController {

    private final TaskStatsService taskStatsService;
    private final UserService userService;
    private final TaskQueryService taskQueryService;

    public DashboardApiController(TaskStatsService taskStatsService,
                                  UserService userService,
                                  TaskQueryService taskQueryService) {
        this.taskStatsService = taskStatsService;
        this.userService = userService;
        this.taskQueryService = taskQueryService;
    }

    // 🔹 Helper to get logged-in user safely
    private User getCurrentUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername());
    }

    /**
     * 📊 Task count by date (for charts)
     * Example:
     * {
     *   "2026-02-01": 4,
     *   "2026-02-02": 7
     * }
     */
    @GetMapping("/stats")
    public Map<String, Long> getTaskStats(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getCurrentUser(userDetails);

        return taskStatsService.getTaskStats(user)
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        Map.Entry::getValue
                ));
    }

   
    /**
     * ✅ Completed tasks count
     */
    @GetMapping("/completed")
    public long getCompletedTaskCount(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getCurrentUser(userDetails);
        return taskStatsService.getCompletedTaskCount(user);
    }

    /**
     * ⏳ Pending tasks count (optimized)
     */
    @GetMapping("/pending")
    public long getPendingTaskCount(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getCurrentUser(userDetails);

        // 🔥 IMPORTANT: use optimized DB count method
        return taskStatsService.getPendingTaskCount(user);
    }
    
    @GetMapping("/top5")
    @ResponseBody
    public List<TaskResponseDTO> getTop5Tasks(@AuthenticationPrincipal UserDetails springUser) {
        User user = userService.getUserWithAllData(springUser.getUsername());
        return taskQueryService.getTop5TodayTasks(user);
    }
}