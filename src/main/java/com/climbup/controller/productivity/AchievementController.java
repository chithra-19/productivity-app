package com.climbup.controller.productivity;

import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.model.User;
import com.climbup.model.UserAchievement;
import com.climbup.repository.UserAchievementRepository;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.user.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService achievementService;
    private final UserService userService;
    private final UserAchievementRepository userAchievementRepository;

    public AchievementController(AchievementService achievementService,
                                 UserService userService,
                                 UserAchievementRepository userAchievementRepository) {
        this.achievementService = achievementService;
        this.userService = userService;
        this.userAchievementRepository = userAchievementRepository;
    }

    private User currentUser() {
        return userService.getCurrentUser();
    }

    // 🔹 Main endpoint for frontend (split view: custom vs default)
    @GetMapping
    public ResponseEntity<Map<String, List<AchievementResponseDTO>>> getAchievements(@AuthenticationPrincipal User user) {
        Map<String, List<AchievementResponseDTO>> achievements = new HashMap<>();
        achievements.put("customGoals", achievementService.getUserGoals(user));
        achievements.put("defaultTemplates", achievementService.getTemplateGoals(user));
        return ResponseEntity.ok(achievements);
    }




    // 🔹 Flat list endpoint (optional, for notifications or evaluation)
    @GetMapping("/list")
    public ResponseEntity<List<AchievementResponseDTO>> getUserAchievements() {
        return ResponseEntity.ok(achievementService.getUserAchievements(currentUser()));
    }

    @GetMapping("/new")
    public ResponseEntity<Boolean> hasNewAchievements() {
        return ResponseEntity.ok(achievementService.hasNew(currentUser()));
    }

    @PostMapping("/seen")
    public ResponseEntity<List<AchievementResponseDTO>> markSeen() {
        achievementService.markSeen(currentUser());
        return ResponseEntity.ok(achievementService.getUserAchievements(currentUser()));
    }

    @PostMapping("/evaluate")
    public ResponseEntity<List<AchievementResponseDTO>> evaluate() {
        achievementService.evaluate(currentUser());
        return ResponseEntity.ok(achievementService.getUserAchievements(currentUser()));
    }

    @GetMapping("/refresh")
    public ResponseEntity<List<AchievementResponseDTO>> refresh() {
        User user = currentUser();
        achievementService.evaluate(user);
        return ResponseEntity.ok(achievementService.getUserAchievements(user));
    }
}


/*If they ask:

“How did you design your AchievementController?”

Say:

“I kept the controller thin by delegating all logic to the service
 layer and used DTOs to avoid exposing internal entities.
  It only handles request routing and response mapping.”*/