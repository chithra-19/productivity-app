package com.climbup.controller.task;


import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.response.DashboardResponseDTO;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.service.productivity.*;
import com.climbup.service.task.ActivityService;
import com.climbup.service.task.DashboardService;
import com.climbup.service.task.TaskQueryService;
import com.climbup.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserService userService;
    private final TaskQueryService taskQueryService;
    private final MotivationService motivationService;
    private final DashboardService dashboardService;
    private final ActivityService activityService;
 
   

    
    @Autowired
    public DashboardController(UserService userService,
                                   TaskQueryService taskQueryService,
                                   MotivationService motivationService,
                                   DashboardService dashboardService,
                                   ActivityService activityService) {
        this.userService = userService;
        this.taskQueryService = taskQueryService;
        this.motivationService = motivationService;
        this.dashboardService = dashboardService;
        this.activityService = activityService;
                
    }

    // Load common items
    @ModelAttribute
    public void preloadCommonAttributes(Model model,
                                        @AuthenticationPrincipal UserDetails springUser,
                                        HttpServletRequest request) {

        if (springUser != null) {
            User user = userService.getUserWithAllData(springUser.getUsername());
            model.addAttribute("username", user.getUsername());
            model.addAttribute("task", new TaskRequestDTO());
            model.addAttribute("currentPath", request.getRequestURI());
        }
    }

    @GetMapping
    public String showDashboard(@AuthenticationPrincipal UserDetails springUser,
                                Model model) {

        if (springUser == null) {
            return "redirect:/login";
        }

        User user = userService.getUserWithAllData(springUser.getUsername());

        String displayName = "User"; // fallback

        if (user.getProfile() != null &&
            user.getProfile().getFirstName() != null &&
            !user.getProfile().getFirstName().isBlank()) {

            displayName = user.getProfile().getFirstName();
        }

        model.addAttribute("displayName", displayName);
        
        model.addAttribute("firstName", displayName);
        // ONLY ONE DTO
        DashboardResponseDTO dashboard = dashboardService.buildDashboard(user);

        model.addAttribute("dashboard", dashboard);

        // keep ONLY UI-only data if needed
        model.addAttribute("quote", motivationService.getRandomQuote());
        
        List<Task> todayTasks = taskQueryService.getTasksDueOn(user, LocalDate.now());

        model.addAttribute("todaysTasks", todayTasks);
        model.addAttribute("todaysCount", todayTasks.size());

        model.addAttribute("recentActivities",
                activityService.getRecentActivities(user));

        return "dashboard";
    }

 
}
