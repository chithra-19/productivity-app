package com.climbup.controller.productivity;

import com.climbup.dto.request.StreakTrackerRequestDTO;
import com.climbup.dto.response.StreakTrackerResponseDTO;
import com.climbup.model.StreakTracker;
import com.climbup.model.User;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/streaks")
public class StreakTrackerController {

    private final StreakTrackerService streakService;
    private final UserService userService; // For getting current user

    @Autowired
    public StreakTrackerController(StreakTrackerService streakService, UserService userService) {
        this.streakService = streakService;
        this.userService = userService;
    }

    /**
     * Mark/update a streak for a category
     */
    @PostMapping("/update")
    public ResponseEntity<StreakTrackerResponseDTO> updateStreak(@RequestBody StreakTrackerRequestDTO request) {
        User currentUser = userService.getCurrentUser(); // implement this in UserService
        StreakTracker tracker = streakService.updateStreak(currentUser, request.getCategory());

        return ResponseEntity.ok(mapToResponseDTO(tracker));
    }

    /**
     * Get streak by category for current user
     */
    @GetMapping("/{category}")
    public ResponseEntity<StreakTrackerResponseDTO> getStreak(@PathVariable String category) {
        User currentUser = userService.getCurrentUser();
        StreakTracker tracker = streakService.getStreakByUserAndCategory(currentUser.getId(), category);

        return ResponseEntity.ok(mapToResponseDTO(tracker));
    }

    /**
     * Get all streaks for current user
     */
    @GetMapping
    public ResponseEntity<List<StreakTrackerResponseDTO>> getAllStreaks() {
        User currentUser = userService.getCurrentUser();
        List<StreakTracker> streaks = streakService.getAllStreaksForUser(currentUser.getId());

        List<StreakTrackerResponseDTO> responseList = streaks.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    /**
     * Utility to convert entity to DTO
     */
    private StreakTrackerResponseDTO mapToResponseDTO(StreakTracker tracker) {
        StreakTrackerResponseDTO dto = new StreakTrackerResponseDTO();
        dto.setId(tracker.getId());
        dto.setCategory(tracker.getCategory());
        dto.setCurrentStreak(tracker.getCurrentStreak());
        dto.setLongestStreak(tracker.getLongestStreak());
        dto.setLastActiveDate(tracker.getLastActiveDate());
        return dto;
    }
}
