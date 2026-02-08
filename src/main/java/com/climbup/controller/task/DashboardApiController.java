package com.climbup.controller.task;

import com.climbup.model.User;
import com.climbup.service.task.TaskService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    private final TaskService taskService;

    public DashboardApiController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 📊 Task count by date (for heatmap / charts)
     * Response example:
     * {
     *   "2026-02-01": 4,
     *   "2026-02-02": 7
     * }
     */
    @GetMapping("/task-stats")
    public Map<String, Long> getTaskStats(
            @AuthenticationPrincipal User user
    ) {
        return taskService.getTaskStats(user)
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(), // LocalDate → String
                        Map.Entry::getValue
                ));
    }

    /**
     * 🔥 Heatmap data (based on due dates)
     * Response example:
     * {
     *   "2026-02-03": 2,
     *   "2026-02-05": 1
     * }
     */
    @GetMapping("/heatmap")
    public Map<String, Integer> getHeatmapData(
            @AuthenticationPrincipal User user
    ) {
        return taskService.getTaskCountsByDate(user)
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue().intValue()
                ));
    }

    /**
     * ✅ Completed task count
     */
    @GetMapping("/completed-count")
    public long getCompletedTaskCount(
            @AuthenticationPrincipal User user
    ) {
        return taskService.getCompletedTaskCount(user);
    }

    /**
     * ⏳ Pending tasks count
     */
    @GetMapping("/pending-count")
    public long getPendingTaskCount(
            @AuthenticationPrincipal User user
    ) {
        return taskService.getPendingTasks(user).size();
    }
}
