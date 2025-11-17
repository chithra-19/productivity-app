package com.climbup.controller.task;

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.response.HeatmapDTO;
import com.climbup.model.User;
import com.climbup.repository.TaskRepository;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.task.TaskService;
import com.climbup.service.user.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardViewController {

    private final UserService userService;
    private final TaskService taskService;
    private final StreakTrackerService streakTrackerService;
    private final AchievementService achievementService;
    private final TaskRepository taskRepository;

    @Autowired
    public DashboardViewController(UserService userService,
                                   TaskService taskService,
                                   StreakTrackerService streakTrackerService,
                                   AchievementService achievementService,
                                   TaskRepository taskRepository) {
        this.userService = userService;
        this.taskService = taskService;
        this.streakTrackerService = streakTrackerService;
        this.achievementService = achievementService;
        this.taskRepository = taskRepository;
    }

    @ModelAttribute
    public void preloadCommonAttributes(Model model, Principal principal, HttpServletRequest request) {
        if (principal != null) {
            User user = userService.getUserWithAllData(principal.getName());
            model.addAttribute("username", user.getUsername());
            model.addAttribute("quote", "Success starts with self-discipline.");
            model.addAttribute("task", new TaskRequestDTO());
            model.addAttribute("currentPath", request.getRequestURI());
        }
    }
    
    @GetMapping("")
    public String showDashboard(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String login = principal.getName();

        // Load user with tasks/goals/achievements/streaks
        User user = userService.getUserWithAllData(login);

        // Streak values
        int currentStreak = streakTrackerService.calculateCurrentStreak(user.getId());
        int bestStreak = streakTrackerService.getBestStreak(user.getId());

        // Productivity score
        int score = achievementService.getProductivityScore(user);

        // Pending tasks count
        int pendingCount = (int) taskService.getTasksForUser(user)
                .stream()
                .filter(task -> !task.isCompleted())
                .count();

        // Heatmap data
        List<HeatmapDTO> heatmapData = taskService.getHeatmapData(user);

        // Add attributes
        model.addAttribute("user", user);
        model.addAttribute("currentStreak", currentStreak);
        model.addAttribute("bestStreak", bestStreak);
        model.addAttribute("score", score);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("heatmapData", heatmapData);
        model.addAttribute("pendingTasks", taskRepository.findByUserAndCompletedFalse(user));

        return "dashboard";
    }
    
    @GetMapping("/goals")
    public String showGoalsPage(Model model, Principal principal, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "goals";
    }

    @GetMapping("/streaks")
    public String showStreaksPage(Model model, Principal principal, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "streak-tracker";
    }

    @GetMapping("/achievements")
    public String showAchievementsPage(Model model, Principal principal, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "achievements";
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model, Principal principal, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "profile";
    }
}
