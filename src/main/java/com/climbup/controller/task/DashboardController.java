package com.climbup.controller.task;

import com.climbup.model.User;
import com.climbup.model.ActivityLog;
import com.climbup.dto.response.HeatmapResponse;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.productivity.ActivityLogService;
import com.climbup.service.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final ActivityLogService activityLogService;
    private final UserService userService;
    private final AchievementService achievementService;

    @Autowired
    public DashboardController(ActivityLogService activityLogService,
                               UserService userService,
                               AchievementService achievementService) {
        this.activityLogService = activityLogService;
        this.userService = userService;
        this.achievementService = achievementService;
    }

    // 🧭 Dashboard view (Thymeleaf template)
    @GetMapping("/api/dashboard/view/{userId}")
    public String getDashboardView(@PathVariable Long userId, Model model) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("achievements", achievementService.getUserAchievements(user));
        model.addAttribute("heatmapData", activityLogService.getHeatmapData(user));
        return "dashboard";
    }

    // 📅 Heatmap API: structured activity data for logged-in user
    @GetMapping("/api/activity-log/{category}")
    public ResponseEntity<HeatmapResponse> getActivityDates(
            @PathVariable String category,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal principal) {

        User user = userService.findByUsername(principal.getName());
        List<ActivityLog> logs = activityLogService.getLogs(user, category, from, to);

        List<String> activeDates = logs.stream()
                .map(ActivityLog::getActivityDate)
                .map(LocalDate::toString)
                .distinct()
                .collect(Collectors.toList());

        int currentStreak = activityLogService.getCurrentStreak(user, category);

        HeatmapResponse response = new HeatmapResponse();
        response.setActiveDates(activeDates);
        response.setTotalDays(activeDates.size());
        response.setCurrentStreak(currentStreak);

        return ResponseEntity.ok(response);
    }
}
