package com.climbup.controller.productivity;

import com.climbup.dto.request.AchievementRequestDTO;
import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.model.User;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService achievementService;
    private final UserService userService;

    public AchievementController(AchievementService achievementService, UserService userService) {
        this.achievementService = achievementService;
        this.userService = userService;
    }

    // ---------------- Create a new achievement (admin or system use) ----------------
    @PostMapping("/create")
    public ResponseEntity<AchievementResponseDTO> createAchievement(
            @Valid @RequestBody AchievementRequestDTO dto) {

        User currentUser = userService.getCurrentUser();
        AchievementResponseDTO response = achievementService.createAchievement(dto, currentUser);
        return ResponseEntity.ok(response);
    }

    // ---------------- Get all achievements for current user ----------------
    @GetMapping
    public ResponseEntity<List<AchievementResponseDTO>> getUserAchievements() {
        User currentUser = userService.getCurrentUser();
        List<AchievementResponseDTO> achievements = achievementService.getUserAchievements(currentUser);
        return ResponseEntity.ok(achievements);
    }

    // ---------------- Unlock an achievement manually ----------------
    @PostMapping("/{achievementId}/unlock")
    public ResponseEntity<AchievementResponseDTO> unlockAchievement(@PathVariable Long achievementId) {
        User currentUser = userService.getCurrentUser();
        AchievementResponseDTO unlocked = achievementService.unlockAchievement(achievementId, currentUser);
        return ResponseEntity.ok(unlocked);
    }

    // ---------------- Check if user has new achievements ----------------
    @GetMapping("/new")
    public ResponseEntity<Boolean> hasNewAchievements() {
        User currentUser = userService.getCurrentUser();
        boolean hasNew = achievementService.hasNewAchievement(currentUser);
        return ResponseEntity.ok(hasNew);
    }

    // ---------------- Mark newly unlocked achievements as seen ----------------
    @PostMapping("/mark-seen")
    public ResponseEntity<Void> markAchievementsAsSeen() {
        User currentUser = userService.getCurrentUser();
        achievementService.markAchievementsAsSeen(currentUser);
        return ResponseEntity.ok().build();
    }

    // ---------------- Thymeleaf page rendering for dashboard ----------------
    @GetMapping("/dashboard")
    public String achievementsPage(Model model, @AuthenticationPrincipal User user) {
        List<AchievementResponseDTO> achievements = achievementService.getUserAchievements(user);
        List<AchievementResponseDTO> newAchievements = achievements.stream()
                .filter(a -> a.isUnlocked() && !a.isSeen())
                .collect(Collectors.toList());

        model.addAttribute("achievements", achievements);
        model.addAttribute("newAchievements", newAchievements);
        return "achievements"; // Thymeleaf template name
    }
}
