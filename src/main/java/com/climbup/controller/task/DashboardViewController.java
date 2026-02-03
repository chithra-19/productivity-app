package com.climbup.controller.task;

import com.climbup.dto.request.FocusSessionRequestDTO;
import com.climbup.dto.request.ProfileRequestDTO;
import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.response.FocusSessionResponseDTO;
import com.climbup.model.FocusSession;
import com.climbup.model.Goal;
import com.climbup.model.Profile;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.service.productivity.*;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardViewController {

    private final UserService userService;
    private final TaskService taskService;
    private final StreakTrackerService streakTrackerService;
    private final AchievementService achievementService;
    private final ProfileService profileService;
    private final MotivationService motivationService;
    private final FocusSessionService focusSessionService;

    @Autowired
    public DashboardViewController(UserService userService,
                                   TaskService taskService,
                                   StreakTrackerService streakTrackerService,
                                   AchievementService achievementService,
                                   ProfileService profileService,
                                   MotivationService motivationService,
                                   FocusSessionService focusSessionService,
                                   GoalService goalService) {
        this.userService = userService;
        this.taskService = taskService;
        this.streakTrackerService = streakTrackerService;
        this.achievementService = achievementService;
        this.profileService = profileService;
        this.motivationService = motivationService;
        this.focusSessionService = focusSessionService;
        this.goalService = goalService;
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

        User user = userService.getUserWithAllData(springUser.getUsername());

        // Motivation
        model.addAttribute("quote", motivationService.getRandomQuote());

        // Streaks
        model.addAttribute("currentStreak", streakTrackerService.calculateCurrentStreak(user.getId()));
        model.addAttribute("bestStreak", streakTrackerService.getBestStreak(user.getId()));

        List<Goal> goals = goalService.getGoalsForUser(user);
        model.addAttribute("goals", goals);
        // Tasks for today
        List<Task> today = taskService.getTasksDueOn(user, LocalDate.now());
        model.addAttribute("pendingCount", (int) today.stream().filter(t -> !t.isCompleted()).count());
        model.addAttribute("todaysTasks", today);
        model.addAttribute("todaysCount", today.size());
        model.addAttribute("pendingTasks", taskService.getPendingTasks(user));

        // Heatmap: convert LocalDate → String, Long → Integer
        Map<String, Integer> heatmapData = taskService.getTaskCountsByDate(user)
                .entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey() != null ? e.getKey().toString() : "",
                        e -> e.getValue() != null ? e.getValue().intValue() : 0
                ));
        model.addAttribute("heatmapData", heatmapData);

        // Focus session
        FocusSession currentSession = focusSessionService.getCurrentSession(user);
        model.addAttribute("currentSession", currentSession);
        model.addAttribute("completedPomodoros", focusSessionService.getCompletedSessionsCount(user));
        model.addAttribute("currentPomodoroRemaining",
                currentSession != null ? focusSessionService.getRemainingMinutes(currentSession) : 0);

        return "dashboard";
    }


    @GetMapping("/goals")
    public String showGoalsPage(HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "goals";
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
                new FocusSessionRequestDTO(25, FocusSession.SessionType.POMODORO, ""),
                user
        );
    }
    @GetMapping("/focus-sessions")
    public String focusSessionsPage() {
        return "focus-sessions";
    }

}
