package com.climbup.controller.user;

import com.climbup.dto.request.ProfileRequestDTO;
import com.climbup.dto.response.ProfileResponseDTO;
import com.climbup.model.ActivityLog;
import com.climbup.model.User;
import com.climbup.service.productivity.*;
import com.climbup.service.task.TaskService;
import com.climbup.service.user.ProfileService;
import com.climbup.service.user.UserService;

import jakarta.validation.Valid;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/dashboard/profile")
public class ProfileViewController {

    private final ProfileService profileService;

    @Autowired private UserService userService;
    @Autowired private ActivityLogService activityLogService;
    @Autowired private TaskService taskService;
    @Autowired private FocusSessionService focusService;
    @Autowired private StreakTrackerService streakService;
    @Autowired private BadgeService badgeService;

    public ProfileViewController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // -----------------------------------------------------------
    // VIEW PROFILE
    // -----------------------------------------------------------
    @GetMapping("/{userId}")
    public String showProfile(@PathVariable Long userId, Model model) {

        User user = userService.findById(userId);
        if (user == null) {
            return "redirect:/dashboard?error=user_not_found";
        }

        ProfileResponseDTO profile = profileService.getProfile(userId);

        // Preload data for UI
        ProfileRequestDTO dto = new ProfileRequestDTO();
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setEmail(profile.getEmail());
        dto.setBio(profile.getBio());
        dto.setProfilePictureUrl(profile.getProfilePictureUrl());

        int streak = streakService.getCurrentStreak(user);
        int completedTasks = taskService.countCompletedTasks(userId);
        int focusMinutes = focusService.getTotalFocusMinutes(user);
        int productivityScore = profile.getProductivityScore();

        var recentActivities = activityLogService.getRecentActivities(user, 10);
        var badges = badgeService.getUserBadges(user);

        model.addAttribute("profile", profile);
        model.addAttribute("profileRequestDTO", dto);

        model.addAttribute("streak", streak);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("focusMinutes", focusMinutes);
        model.addAttribute("productivityScore", productivityScore);

        model.addAttribute("badges", badges);
        model.addAttribute("badgesCount", badges.size());
        model.addAttribute("recentActivities", recentActivities);

        model.addAttribute("editMode", false); // NORMAL VIEW MODE
        return "profile";
    }

    // -----------------------------------------------------------
    // EDIT PROFILE PAGE
    // -----------------------------------------------------------
    @GetMapping("/{userId}/edit")
    public String editProfile(@PathVariable Long userId, Model model) {

        ProfileResponseDTO profile = profileService.getProfile(userId);

        ProfileRequestDTO dto = new ProfileRequestDTO();
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setEmail(profile.getEmail());
        dto.setBio(profile.getBio());
        dto.setProfilePictureUrl(profile.getProfilePictureUrl());

        model.addAttribute("profileRequestDTO", dto);
        model.addAttribute("userId", userId);
        model.addAttribute("editMode", true); // ENABLE EDIT MODE

        return "profile";  // same HTML but in edit mode
    }

    // -----------------------------------------------------------
    // UPDATE PROFILE ACTION
    // -----------------------------------------------------------
    @PostMapping("/{userId}/update")
    public String updateProfile(@PathVariable Long userId,
                                @Valid @ModelAttribute("profileRequestDTO") ProfileRequestDTO dto,
                                BindingResult result,
                                @RequestParam("profilePictureFile") MultipartFile file,
                                Model model) {

        if (result.hasErrors()) {
            // reload edit mode with errors
            model.addAttribute("editMode", true);
            model.addAttribute("userId", userId);
            return "profile";
        }

        if (!file.isEmpty()) {
            String imageUrl = profileService.saveProfileImage(file);
            dto.setProfilePictureUrl(imageUrl);
        }

        profileService.updateProfile(userId, dto);

        return "redirect:/dashboard/profile/" + userId + "?updated=true";
    }
    
    // ---------------------- JSON ENDPOINT ----------------------
    @GetMapping("/recent")
    @ResponseBody
    public List<ActivityLog> getRecentActivities(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return activityLogService.getRecentActivities(user, 10);
    }

}
