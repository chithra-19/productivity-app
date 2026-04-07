package com.climbup.controller.task;

import com.climbup.dto.request.FocusSessionRequestDTO;
import com.climbup.dto.request.GoalRequestDTO;
import com.climbup.dto.request.ProfileRequestDTO;
import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.response.ActivityDTO;
import com.climbup.dto.response.DashboardSummaryDTO;
import com.climbup.dto.response.FocusSessionResponseDTO;

import com.climbup.model.FocusSession;
import com.climbup.model.Goal;
import com.climbup.model.Profile;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.service.productivity.*;
import com.climbup.service.task.ActivityService;
import com.climbup.service.task.DashboardService;
import com.climbup.service.task.TaskService;
import com.climbup.service.user.ProfileService;
import com.climbup.service.user.UserService;
import com.climbup.service.productivity.GoalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


import java.security.Principal;
import java.time.LocalDate;
import java.util.List;


@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserService userService;
    private final TaskService taskService;
    private final ProfileService profileService;
    private final MotivationService motivationService;
    private final FocusSessionService focusSessionService;
    private final GoalService goalService;
    private final DashboardService dashboardService;
    private final ActivityService activityService;
    private final XPService xpService;
    private final StreakTrackerService streakTrackerService;

    
    @Autowired
    public DashboardController(UserService userService,
                                   TaskService taskService,
                                   ProfileService profileService,
                                   MotivationService motivationService,
                                   FocusSessionService focusSessionService,
                                   GoalService goalService,
                                   DashboardService dashboardService,
                                   ActivityService activityService,
                                   XPService xpService,
                                   StreakTrackerService streakTrackerService) {
        this.userService = userService;
        this.taskService = taskService;
        this.profileService = profileService;
        this.motivationService = motivationService;
        this.focusSessionService = focusSessionService;
        this.goalService = goalService;
        this.dashboardService = dashboardService;
        this.activityService = activityService;
        this.xpService = xpService;
        this.streakTrackerService = streakTrackerService;
        
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
    public String showDashboard(Model model,
                                @AuthenticationPrincipal UserDetails springUser) {
    	

        if (springUser == null) {
            return "redirect:/login"; // 🔥 prevent crash
        }
    	User user = userService.getUserWithAllData(springUser.getUsername());

    	String firstName = null;
    	if (user.getProfile() != null) {
    	    firstName = user.getProfile().getFirstName();
    	}

    	System.out.println("Resolved firstName: " + firstName); // ✅ Debug print

    	model.addAttribute("firstName", firstName);
    	
        int xpInLevel = xpService.xpProgressInCurrentLevel(user);
        int level = xpService.getLevel(user.getId());
        double xpPercent = xpService.getXpPercentage(user.getId());

        model.addAttribute("level", level);
        model.addAttribute("xpInLevel", xpInLevel);
        model.addAttribute("xpPercent", xpPercent);
        
        // 🔥 CENTRALIZED DASHBOARD DATA
        DashboardSummaryDTO summary = dashboardService.getDashboardSummary(user);

        if (summary == null) {
            summary = new DashboardSummaryDTO();
            summary.setCurrentStreak(0);
            summary.setBestStreak(0);
        }

        model.addAttribute("summary", summary);
        // Motivation
        model.addAttribute("quote", motivationService.getRandomQuote());

        model.addAttribute("goals", 
            goalService.getGoalsForUser(user) != null ? 
            goalService.getGoalsForUser(user) : List.of());

       
        // Tasks
        List<Task> today = taskService.getTasksDueOn(user, LocalDate.now());
        model.addAttribute("todaysTasks", today != null ? today : List.of());
       
        model.addAttribute("todaysCount", today.size());
        model.addAttribute("pendingCount",
                (int) today.stream().filter(t -> !t.isCompleted()).count());

        List<ActivityDTO> recentActivities = activityService.getRecentActivities(user);
        model.addAttribute("recentActivities", 
                recentActivities != null ? recentActivities : List.of());

        // Focus
        FocusSession currentSession = focusSessionService.getCurrentSession(user);
        model.addAttribute("currentSession", currentSession);
        model.addAttribute("completedPomodoros",
                focusSessionService.getCompletedSessionsCount(user));

        return "dashboard";
    }
   
    @GetMapping("/goals")
    public String showGoalsPage(@AuthenticationPrincipal UserDetails springUser,
                                HttpServletRequest request,
                                Model model) {

        User user = userService.getUserWithAllData(springUser.getUsername());

        model.addAttribute("goals", goalService.getGoalsForUser(user));
        model.addAttribute("goal", new GoalRequestDTO()); // form binding
        model.addAttribute("currentPath", request.getRequestURI());

        return "goals";
    }
    
    @PostMapping("/goals/save")
    public String saveGoal(@ModelAttribute GoalRequestDTO dto,
                           @AuthenticationPrincipal UserDetails springUser) {

        User user = userService.findByEmail(springUser.getUsername());

        Goal goal = new Goal();
        goal.setTitle(dto.getTitle());
        goal.setDescription(dto.getDescription());
        goal.setDueDate(dto.getDueDate());
        goal.setPriority(dto.getPriority());
        goal.setStatus(dto.getStatus());
        goal.setUser(user);

        goalService.saveGoal(goal);

        return "redirect:/dashboard/goals";
    }
    
    @GetMapping("/streaks")
    public String showStreaksPage(HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "streak-tracker";
    }

    @GetMapping("/achievements")
    public String showAchievementsPage(HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "achievements";
    }

    @GetMapping("/profile")
    public String showProfilePage(@AuthenticationPrincipal UserDetails springUser,
                                  Model model,
                                  HttpServletRequest request) {

        User user = userService.findByEmail(springUser.getUsername());
        Profile profile = profileService.getOrCreateProfile(user);

        ProfileRequestDTO dto = new ProfileRequestDTO();
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setEmail(profile.getEmail());
        dto.setBio(profile.getBio());
        dto.setProfilePictureUrl(profile.getProfilePictureUrl());

        model.addAttribute("profile", profile);
        model.addAttribute("profileRequestDTO", dto);
        model.addAttribute("currentPath", request.getRequestURI());

        return "profile";
    }
    @GetMapping("/focus-mode")
    public String showFocusMode(@AuthenticationPrincipal UserDetails springUser,
                                Model model) {

        User user = userService.getUserWithAllData(springUser.getUsername());
        FocusSession currentSession = focusSessionService.getCurrentSession(user);

        model.addAttribute("user", user); // 🔥 REQUIRED
        model.addAttribute("currentSession", currentSession);
        model.addAttribute("remainingMinutes",
                currentSession != null ? focusSessionService.getRemainingMinutes(currentSession) : 0);

        return "focus-mode";
    }

    @PostMapping("/focus-mode/start")
    public @ResponseBody FocusSessionResponseDTO startFocusModeSession(
            @AuthenticationPrincipal UserDetails springUser) {

        User user = userService.getUserWithAllData(springUser.getUsername());

        return focusSessionService.startSession(
                new FocusSessionRequestDTO(25, FocusSession.SessionType.FOCUS, ""),
                user
        );
    }
    @GetMapping("/focus-sessions")
    public String focusSessionsPage() {
        return "focus-sessions";
    }
    // 🔹 Helper method to fetch logged-in user
    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("No authenticated user found");
        }
        return userService.findByEmail(principal.getName());
    }

    @GetMapping("/task-all")
    public String getAllTasks(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        model.addAttribute("tasks", taskService.getTasksForUser(user));
        return "tasks/task-all";
    }
   
    @GetMapping("/task-today")
    public String getTodayTasks(Model model, Principal principal) {
        User user = getCurrentUser(principal);

        List<Task> todayTasks = taskService.getTasksDueOn(user, LocalDate.now());
        model.addAttribute("tasks", todayTasks);

        return "tasks/task-today";
    }
   
    @GetMapping("/add-task")
    public String getAddTaskPage(Model model) {
        model.addAttribute("task", new TaskRequestDTO());
        return "tasks/add-task";
    }
    @GetMapping("/activities")
    public ResponseEntity<List<ActivityDTO>> getActivities(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<ActivityDTO> activities = activityService.getRecentActivities(user); // Already DTOs
        return ResponseEntity.ok(activities);
    }

}
