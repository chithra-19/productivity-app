package com.climbup.controller.user;

import com.climbup.dto.request.ProfileRequestDTO;
import com.climbup.dto.response.ProfileResponseDTO;
import com.climbup.model.*;
import com.climbup.service.productivity.*;
import com.climbup.service.storage.FileStorageService;
import com.climbup.service.task.TaskStatsService;
import com.climbup.service.user.ProfileService;
import com.climbup.service.user.UserService;

import jakarta.validation.Valid;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/dashboard/profile")
public class ProfileViewController {

    private final ProfileService profileService;
    private final UserService userService;
    private final TaskStatsService taskStatsService;
    private final FileStorageService fileStorageService;
    private final StreakTrackerService streakService;
    private final XPService xpService;
    private final BadgeService badgeService;
    private final ActivityLogService activityLogService;

    public ProfileViewController(ProfileService profileService,
                                 UserService userService,
                                 TaskStatsService taskStatsService,
                                 FileStorageService fileStorageService,
                                 StreakTrackerService streakService,
                                 XPService xpService,
                                 BadgeService badgeService,
                                 ActivityLogService activityLogService) {

        this.profileService = profileService;
        this.userService = userService;
        this.taskStatsService = taskStatsService;
        this.fileStorageService = fileStorageService;
        this.streakService = streakService;
        this.xpService = xpService;
        this.badgeService = badgeService;
        this.activityLogService = activityLogService;
    }

    // =========================================================
    // VIEW PROFILE (ONLY ONE GET MAPPING)
    // =========================================================
    @GetMapping
    public String showProfile(Principal principal, Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName());

        return buildProfile(user, model, false);
    }

    // =========================================================
    // EDIT MODE (same page, different flag)
    // =========================================================
    @GetMapping("/edit")
    public String editProfile(Principal principal, Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName());

        return buildProfile(user, model, true);
    }

    // =========================================================
    // UPDATE PROFILE
    // =========================================================
    @PostMapping("/update")
    public String updateProfile(Principal principal,
                                @Valid @ModelAttribute("profileRequestDTO") ProfileRequestDTO dto,
                                BindingResult result,
                                @RequestParam("profilePictureFile") MultipartFile file,
                                Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName());

        if (result.hasErrors()) {
            return buildProfile(user, model, true);
        }

        if (file != null && !file.isEmpty()) {
            String imageUrl = fileStorageService.uploadProfileImage(file);
            dto.setProfilePictureUrl(imageUrl);
        }

        profileService.updateProfile(user.getId(), dto);

        return "redirect:/dashboard/profile?updated=true";
    }

    // =========================================================
    // CORE METHOD (NO DUPLICATION)
    // =========================================================
    
    private String buildProfile(User user, Model model, boolean editMode) {

        ProfileResponseDTO profile = profileService.getProfile(user.getId());

        if (profile == null) {
            profile = new ProfileResponseDTO();
        }

        // ===== STREAK =====
        
        int currentStreak = streakService.getCurrentStreak(user);
        int bestStreak = streakService.getBestStreak(user);
        // ===== TASKS =====
        long completedTasks = taskStatsService.getCompletedTaskCount(user);

        // ===== XP =====
        long totalXP = xpService.getCurrentXP(user.getId());
        int level = xpService.calculateLevel((int) totalXP);
        int xpForNextLevel = xpService.xpRequiredForNextLevel(level);

        // XP earned within current level
        int currentXP = (int) (totalXP % xpForNextLevel);
        int xpPercentage = (int) ((double) currentXP / xpForNextLevel * 100);
        // ===== DTO (IMPORTANT FIX) =====
        if (!model.containsAttribute("profileRequestDTO")) {
            ProfileRequestDTO dto = new ProfileRequestDTO();
            dto.setFirstName(profile.getFirstName());
            dto.setLastName(profile.getLastName());
            dto.setBio(profile.getBio());
            dto.setProfilePictureUrl(profile.getProfilePictureUrl());

            model.addAttribute("profileRequestDTO", dto);
        }

        // ===== VIEW MAP (SAFE VERSION) =====
        Map<String, Object> view = new HashMap<>();

        view.put("profile", profile);

        view.put("dashboard", Map.of(
            "currentStreak", currentStreak,
            "bestStreak", bestStreak
        ));

        view.put("completedTasks", completedTasks);
        view.put("level", level);
        view.put("currentXP", currentXP);
        view.put("xpPercentage", xpPercentage);
        view.put("xpForNextLevel", xpForNextLevel);

        model.addAttribute("view", view);
        model.addAttribute("editMode", editMode);

        return "profile";
    }

 

}