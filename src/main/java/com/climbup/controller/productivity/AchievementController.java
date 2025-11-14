package com.climbup.controller.productivity;

import com.climbup.dto.request.AchievementRequestDTO;
import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.exception.NotFoundException;
import com.climbup.mapper.AchievementMapper;
import com.climbup.model.Achievement;
import com.climbup.model.User;
import com.climbup.repository.AchievementRepository;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/achievements")
public class AchievementController {

	@Autowired
    private final AchievementService achievementService;
    private final UserService userService;
    private final AchievementRepository achievementRepository;

    @Autowired
    public AchievementController(AchievementService achievementService,
                                 UserService userService,
                                 AchievementRepository achievementRepository) {
        this.achievementService = achievementService;
        this.userService = userService;
        this.achievementRepository = achievementRepository;
    }

    // ---------------- Create a new achievement (admin/system) ----------------
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

        // ✅ Fetch ALL achievements for the user
        List<Achievement> all = achievementRepository.findByUser(currentUser);

        List<AchievementResponseDTO> dtoList = all.stream()
                .map(AchievementMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
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

    // ---------------- Thymeleaf page rendering ----------------
    @GetMapping("/dashboard")
    public String achievementsPage(Model model, @AuthenticationPrincipal User user) {
        List<AchievementResponseDTO> achievements = achievementService.getUserAchievements(user);
        List<AchievementResponseDTO> newAchievements = achievements.stream()
                .filter(a -> a.isUnlocked() && !a.isSeen())
                .collect(Collectors.toList());

        model.addAttribute("achievements", achievements);
        model.addAttribute("newAchievements", newAchievements);
        return "achievements"; // Thymeleaf template
    }
}
