package com.climbup.controller.productivity;

import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.mapper.AchievementMapper;
import com.climbup.model.Achievement;
import com.climbup.model.User;
import com.climbup.repository.AchievementRepository;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.user.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService achievementService;
    private final UserService userService;
    private final AchievementRepository achievementRepository;

    public AchievementController(AchievementService achievementService,
                                 UserService userService,
                                 AchievementRepository achievementRepository) {
        this.achievementService = achievementService;
        this.userService = userService;
        this.achievementRepository = achievementRepository;
    }

    // ---------------- Get all achievements for current user ----------------
    @GetMapping
    public ResponseEntity<List<AchievementResponseDTO>> getUserAchievements() {
        User currentUser = userService.getCurrentUser();

        List<Achievement> achievements =
                achievementRepository.findByUser(currentUser);

        List<AchievementResponseDTO> dtoList = achievements.stream()
                .map(AchievementMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    // ---------------- Check if user has newly unlocked achievements ----------------
    @GetMapping("/new")
    public ResponseEntity<Boolean> hasNewAchievements() {
        User currentUser = userService.getCurrentUser();

        boolean hasNew = !achievementRepository
                .findByUserAndNewlyUnlockedTrue(currentUser)
                .isEmpty();

        return ResponseEntity.ok(hasNew);
    }

    // ---------------- Mark newly unlocked achievements as seen ----------------
    @PostMapping("/mark-seen")
    public ResponseEntity<Void> markAchievementsAsSeen() {
        User currentUser = userService.getCurrentUser();
        achievementService.markAchievementsAsSeen(currentUser);
        return ResponseEntity.ok().build();
    }

    // ---------------- Thymeleaf dashboard page ----------------
    @GetMapping("/dashboard")
    public String achievementsPage(Model model,
                                   @AuthenticationPrincipal User user) {

        List<Achievement> achievements =
                achievementRepository.findByUser(user);

        List<AchievementResponseDTO> dtoList = achievements.stream()
                .map(AchievementMapper::toResponseDTO)
                .toList();

        List<AchievementResponseDTO> newAchievements = dtoList.stream()
                .filter(a -> a.isUnlocked() && a.isNewlyUnlocked())
                .toList();

        model.addAttribute("achievements", dtoList);
        model.addAttribute("newAchievements", newAchievements);

        return "achievements";
    }
}
