package com.climbup.controller.task;

import com.climbup.dto.request.ProfileRequestDTO;
import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.response.HeatmapDTO;
import com.climbup.model.Profile;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.TaskRepository;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.productivity.MotivationService;
import com.climbup.service.task.TaskService;
import com.climbup.service.user.ProfileService;
import com.climbup.service.user.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardViewController {

    private final UserService userService;
    private final TaskService taskService;
    private final StreakTrackerService streakTrackerService;
    private final AchievementService achievementService;
    private final TaskRepository taskRepository;
    private final ProfileService profileService;
    private final MotivationService motivationService;
    
    @Autowired
    public DashboardViewController(UserService userService,
                                   TaskService taskService,
                                   StreakTrackerService streakTrackerService,
                                   AchievementService achievementService,
                                   TaskRepository taskRepository, 
                                   ProfileService profileService,
                                   MotivationService motivationService) {
        this.userService = userService;
        this.taskService = taskService;
        this.streakTrackerService = streakTrackerService;
        this.achievementService = achievementService;
        this.taskRepository = taskRepository;
        this.profileService = profileService;
        this.motivationService = motivationService;
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

        String randomQuote = motivationService.getRandomQuote();

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
        List<Task> todaysTasks = taskService.getTasksDueOn(user, LocalDate.now());
        model.addAttribute("todaysTasks", todaysTasks);
        model.addAttribute("todaysCount", todaysTasks.size());
        model.addAttribute("quote", randomQuote);
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
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName());
        Profile profile = profileService.getOrCreateProfile(user);

        // Map Profile → DTO for form binding
        ProfileRequestDTO dto = new ProfileRequestDTO();
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setEmail(profile.getEmail());
        dto.setBio(profile.getBio());
        dto.setProfilePictureUrl(profile.getProfilePictureUrl());

        model.addAttribute("profile", profile);          // for display
        model.addAttribute("profileRequestDTO", dto);    // for form
        model.addAttribute("currentPath", request.getRequestURI());

        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute ProfileRequestDTO profileRequestDTO, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        profileService.updateProfile(user.getId(), profileRequestDTO);
        return "redirect:/dashboard/profile";
    }
    
    
}
