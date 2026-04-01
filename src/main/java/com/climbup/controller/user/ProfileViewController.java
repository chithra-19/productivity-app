package com.climbup.controller.user;

import com.climbup.dto.request.ProfileRequestDTO;
import com.climbup.dto.response.ProfileResponseDTO;
import com.climbup.model.ActivityLog;
import com.climbup.model.Badge;
import com.climbup.model.StreakTracker;
import com.climbup.model.User;
import com.climbup.service.productivity.*;
import com.climbup.service.task.TaskService;
import com.climbup.service.user.ProfileService;
import com.climbup.service.user.UserService;

import jakarta.validation.Valid;

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
    @Autowired private TaskService taskService;
    @Autowired private FocusSessionService focusService;
    @Autowired private StreakTrackerService streakService;
    @Autowired private XPService xpService;
    @Autowired private BadgeService badgeService;
    @Autowired private ActivityLogService activityLogService;

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

        // Fetch profile DTO
        ProfileResponseDTO profile = profileService.getProfile(userId);

        // ----------------- Dynamic stats -----------------
        
        StreakTracker tracker = streakService.getStreakByUserAndCategory(user.getId(), "GLOBAL");

        int currentStreak = tracker != null ? tracker.getCurrentStreak() : 0;
        int bestStreak = tracker != null ? tracker.getLongestStreak() : 0;
        int completedTasks = taskService.countCompletedTasks(userId);
        int productivityScore = profile.getProductivityScore(); // optional

        // XP & Level
        long currentXP = xpService.getCurrentXP(userId);
        int level = xpService.calculateLevel((int) currentXP);
        int xpPercentage = xpService.getProgressToNextLevel(currentXP);

        // --------------------------------------------------

        // DTO for form binding
        ProfileRequestDTO dto = new ProfileRequestDTO();
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setEmail(profile.getEmail());
        dto.setBio(profile.getBio());
        dto.setProfilePictureUrl(profile.getProfilePictureUrl());

        // Badges
        List<Badge> badges = badgeService.getBadgesForUser(user);

        // Recent activities
        List<ActivityLog> recentActivities = activityLogService.getRecentActivities(user);

        // --------- Add all to model ---------
        model.addAttribute("profile", profile); // basic info
        model.addAttribute("profileRequestDTO", dto);

        model.addAttribute("currentStreak", currentStreak);
        model.addAttribute("bestStreak", bestStreak);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("productivityScore", productivityScore);

        model.addAttribute("level", level);
        model.addAttribute("currentXP", currentXP);
        model.addAttribute("xpForNextLevel", xpService.getProgressToNextLevel(currentXP));
        model.addAttribute("xpPercentage", xpPercentage);

        model.addAttribute("badges", badges);
        model.addAttribute("badgesCount", badges.size());
        model.addAttribute("recentActivities", recentActivities);

        model.addAttribute("editMode", false);

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
        model.addAttribute("editMode", true);

        return "profile";
    }

    // -----------------------------------------------------------
    // UPDATE PROFILE
    // -----------------------------------------------------------
    @PostMapping("/{userId}/update")
    public String updateProfile(@PathVariable Long userId,
                                @Valid @ModelAttribute("profileRequestDTO") ProfileRequestDTO dto,
                                BindingResult result,
                                @RequestParam("profilePictureFile") MultipartFile file,
                                Model model) {

        if (result.hasErrors()) {
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
}
