package com.climbup.controller.productivity;

import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.model.User;
import com.climbup.model.UserAchievement;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.user.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService achievementService;
    private final UserService userService;

    public AchievementController(AchievementService achievementService,
                                 UserService userService) {
        this.achievementService = achievementService;
        this.userService = userService;
    }

    private User currentUser() {
        return userService.getCurrentUser();
    }

    // 🔹 Split view (custom + templates)
    @GetMapping
    public ResponseEntity<Map<String, List<AchievementResponseDTO>>> getAchievements() {
        User user = currentUser();

        Map<String, List<AchievementResponseDTO>> response = new HashMap<>();

        // 🔹 Filter out the "CUSTOM_GOAL" template
        List<AchievementResponseDTO> templates = achievementService.getTemplateGoals(user)
            .stream()
            .filter(a -> !"Custom Goal".equalsIgnoreCase(a.getTitle()))
            .toList();

        response.put("customGoals", achievementService.getUserGoals(user));
        response.put("defaultTemplates", templates);

        return ResponseEntity.ok(response);
    }


    // 🔹 Flat list (for UI / debugging)
    @GetMapping("/list")
    public ResponseEntity<List<AchievementResponseDTO>> getUserAchievements() {

        User user = currentUser();
        return ResponseEntity.ok(
                achievementService.getUserAchievements(user.getId())
        );
    }

    // 🔹 Check new achievements
    @GetMapping("/new")
    public ResponseEntity<Boolean> hasNewAchievements() {

        User user = currentUser();
        return ResponseEntity.ok(
                achievementService.hasNew(user)
        );
    }

    // 🔹 Mark as seen
    @PostMapping("/seen")
    public ResponseEntity<List<AchievementResponseDTO>> markSeen() {

        User user = currentUser();

        achievementService.markSeen(user);

        return ResponseEntity.ok(
                achievementService.getUserAchievements(user.getId())
        );
    }

    // 🔹 Evaluate achievements
    @PostMapping("/evaluate")
    public ResponseEntity<List<AchievementResponseDTO>> evaluate() {

        User user = currentUser();

        achievementService.evaluate(user);

        return ResponseEntity.ok(
                achievementService.getUserAchievements(user.getId())
        );
    }

    // 🔹 Refresh endpoint
    @GetMapping("/refresh")
    public ResponseEntity<List<AchievementResponseDTO>> refresh() {

        User user = currentUser();

        achievementService.evaluate(user);

        return ResponseEntity.ok(
                achievementService.getUserAchievements(user.getId())
        );
    }
    
   

}