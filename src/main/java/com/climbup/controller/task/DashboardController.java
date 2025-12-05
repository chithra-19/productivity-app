package com.climbup.controller.task;

import com.climbup.model.Activity;
import com.climbup.model.User;
import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.response.HeatmapDTO;
import com.climbup.dto.response.HeatmapResponse;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.productivity.ActivityLogService;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.task.HeatmapService;
import com.climbup.service.task.TaskService;
import com.climbup.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final ActivityLogService activityLogService;
    private final UserService userService;
    private final AchievementService achievementService;
    private final StreakTrackerService streakTrackerService;
    private final TaskService taskService;
    private final HeatmapService heatmapService;

    @Autowired
    public DashboardController(ActivityLogService activityLogService,
                               UserService userService,
                               AchievementService achievementService,
                               StreakTrackerService streakTrackerService,
                               TaskService taskService,
                               HeatmapService heatmapService) {
        this.activityLogService = activityLogService;
        this.userService = userService;
        this.achievementService = achievementService;
        this.streakTrackerService = streakTrackerService;
        this.taskService = taskService;
        this.heatmapService = heatmapService;
    }


    // 🔹 Helper: fetch current logged-in user safely
    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("No authenticated user found.");
        }

        String email = principal.getName();
        return userService.findByEmail(email);
    }



    /**
     * ✅ Returns the main dashboard view for the logged-in user.
     */
    @GetMapping("/view")
    public String getDashboardView(Model model, Principal principal) {
        User user = getCurrentUser(principal);

        // Ensure user has achievements
        achievementService.initializeAchievements(user);

        // Dashboard stats
        int streak = streakTrackerService.getCurrentStreak(user);
        int score = achievementService.getProductivityScore(user);

        List<TaskResponseDTO> tasks = taskService.getTasksForUser(user);
        int pendingCount = (int) tasks.stream().filter(t -> !t.isCompleted()).count();
        int completedCount = (int) tasks.stream().filter(TaskResponseDTO::isCompleted).count();

        model.addAttribute("user", user);
        model.addAttribute("tasks", tasks);
        model.addAttribute("streak", streak);
        model.addAttribute("score", score);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("quote", "Success starts with self-discipline.");
        model.addAttribute("achievements", achievementService.getUserAchievements(user));
        model.addAttribute("heatmapData", activityLogService.getHeatmapData(user, "task"));
        model.addAttribute("totalTasks", tasks.size());

        return "dashboard";
    }

    /**
     * ✅ REST endpoint: fetch activity log data for given category and date range.
     */
    @GetMapping("/api/activity-log/{category}")
    @ResponseBody
    public ResponseEntity<HeatmapResponse> getActivityDates(
            @PathVariable String category,
            @RequestParam(defaultValue = "30") int days, // optional: default to 30 days
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = getCurrentUser(principal);

        // Map category string to ActivityType enum if needed
        Activity.ActivityType type = null;
        try {
            type = Activity.ActivityType.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            // fallback: treat unknown category as null (all types)
        }

        HeatmapResponse response = heatmapService.buildHeatmapResponse(user.getId(), type, days);

        return ResponseEntity.ok(response);
    }
   

    /**
     * ✅ All tasks page.
     */
    @GetMapping("/task-all")
    public String getAllTasks(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        List<TaskResponseDTO> tasks = taskService.getTasksForUser(user);
        model.addAttribute("tasks", tasks);
        return "tasks/task-all";
    }

    /**
     * ✅ Today's tasks page.
     */
    @GetMapping("/task-today")
    public String getTodayTasks(Model model, Principal principal) {
        User user = getCurrentUser(principal);

        List<TaskResponseDTO> todayTasks = taskService.getTasksForUser(user).stream()
                .filter(t -> t.getDueDate() != null && LocalDate.now().equals(t.getDueDate()))
                .collect(Collectors.toList());

        model.addAttribute("tasks", todayTasks);
        return "tasks/task-today";
    }

    /**
     * ✅ Add-task page.
     */
    @GetMapping("/add-task")
    public String getAddTaskPage(Model model) {
        model.addAttribute("task", new TaskRequestDTO());
        return "tasks/add-task";
    }
    
   
}
