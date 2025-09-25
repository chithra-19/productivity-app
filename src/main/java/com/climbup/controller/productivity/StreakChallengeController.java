package com.climbup.controller.productivity;

import com.climbup.dto.request.UpdateChallengeRequestDTO;
import com.climbup.model.StreakChallenge;
import com.climbup.model.User;
import com.climbup.service.productivity.StreakChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
public class StreakChallengeController {

    private final StreakChallengeService challengeService;

    @Autowired
    public StreakChallengeController(StreakChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    // 🚀 Start a challenge
    @PostMapping("/start")
    public ResponseEntity<StreakChallenge> startChallenge(
            @RequestParam StreakChallenge.ChallengeType type,
            @AuthenticationPrincipal User user) {

        StreakChallenge challenge = challengeService.startChallenge(user, type);
        return ResponseEntity.ok(challenge);
    }

    // ✅ Mark today as completed
    @PostMapping("/complete")
    public ResponseEntity<StreakChallenge> completeToday(
            @RequestParam StreakChallenge.ChallengeType type,
            @AuthenticationPrincipal User user) {

        StreakChallenge challenge = challengeService.markTodayCompleted(user, type);
        return ResponseEntity.ok(challenge);
    }

    // 🔄 Reset a challenge
    @PostMapping("/reset")
    public ResponseEntity<StreakChallenge> resetChallenge(
            @RequestParam StreakChallenge.ChallengeType type,
            @AuthenticationPrincipal User user) {

        StreakChallenge challenge = challengeService.resetChallenge(user, type);
        return ResponseEntity.ok(challenge);
    }

    // 🏆 Get all challenges for logged-in user
    @GetMapping("/me")
    public ResponseEntity<List<StreakChallenge>> getUserChallenges(@AuthenticationPrincipal User user) {
        List<StreakChallenge> challenges = challengeService.getChallengesForUser(user);
        return ResponseEntity.ok(challenges);
    }

    // 🔎 Get single challenge by type for logged-in user
    @GetMapping("/me/{type}")
    public ResponseEntity<StreakChallenge> getChallenge(
            @PathVariable StreakChallenge.ChallengeType type,
            @AuthenticationPrincipal User user) {

        return challengeService.getChallenge(user, type)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✏️ Update a challenge (optional)
    @PatchMapping("/update/{type}")
    public ResponseEntity<StreakChallenge> updateChallenge(
            @PathVariable StreakChallenge.ChallengeType type,
            @AuthenticationPrincipal User user,
            @RequestBody UpdateChallengeRequestDTO updateRequest) {

        StreakChallenge updated = challengeService.updateChallenge(user, type, updateRequest);
        return ResponseEntity.ok(updated);
    }
}
