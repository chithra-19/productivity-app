package com.climbup.controller.task;

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.model.User;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.task.TaskService;
import com.climbup.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserService userService;
    private final AchievementService achievementService;
    private final StreakTrackerService streakTrackerService;
    private final TaskService taskService;

    @Autowired
    public DashboardController(UserService userService,
                               AchievementService achievementService,
                               StreakTrackerService streakTrackerService,
                               TaskService taskService) {
        this.userService = userService;
        this.achievementService = achievementService;
        this.streakTrackerService = streakTrackerService;
        this.taskService = taskService;
    }

    // 🔹 Helper method to fetch logged-in user
    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("No authenticated user found");
        }
        return userService.findByEmail(principal.getName());
    }

    /**
     * ✅ Main dashboard view
     */
    @GetMapping("/view")
    public String getDashboardView(Model model, Principal principal) {
        User user = getCurrentUser(principal);

        // Ensure achievements exist
        achievementService.initializeAchievements(user);

        List<TaskResponseDTO> tasks = taskService.getTasksForUser(user);

        int completedCount = (int) tasks.stream()
                .filter(TaskResponseDTO::isCompleted)
                .count();

        int pendingCount = tasks.size() - completedCount;

        int streak = streakTrackerService.getCurrentStreak(user);

        model.addAttribute("user", user);
        model.addAttribute("tasks", tasks);
        model.addAttribute("streak", streak);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("totalTasks", tasks.size());
        model.addAttribute("achievements", achievementService.getUserAchievements(user));
        model.addAttribute("quote", "Success starts with self-discipline.");

        return "dashboard";
    }

    /**
     * ✅ All tasks page
     */
    @GetMapping("/task-all")
    public String getAllTasks(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        model.addAttribute("tasks", taskService.getTasksForUser(user));
        return "tasks/task-all";
    }

    /**
     * ✅ Today's tasks page
     */
    @GetMapping("/task-today")
    public String getTodayTasks(Model model, Principal principal) {
        User user = getCurrentUser(principal);

        List<TaskResponseDTO> todayTasks = taskService.getTasksForUser(user).stream()
                .filter(task -> task.getDueDate() != null
                        && task.getDueDate().equals(LocalDate.now()))
                .collect(Collectors.toList());

        model.addAttribute("tasks", todayTasks);
        return "tasks/task-today";
    }

    /**
     * ✅ Add task page
     */
    @GetMapping("/add-task")
    public String getAddTaskPage(Model model) {
        model.addAttribute("task", new TaskRequestDTO());
        return "tasks/add-task";
    }
}
