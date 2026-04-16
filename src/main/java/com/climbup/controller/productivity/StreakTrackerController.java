package com.climbup.controller.productivity;

import com.climbup.dto.request.StreakTrackerRequestDTO;
import com.climbup.dto.response.StreakTrackerResponseDTO;
import com.climbup.model.StreakTracker;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.TaskRepository;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/streaks")
public class StreakTrackerController {

    private final StreakTrackerService streakService;
    private final UserService userService; // For getting current user
    
    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    public StreakTrackerController(StreakTrackerService streakService, UserService userService) {
        this.streakService = streakService;
        this.userService = userService;
    }

    /**
     * Mark/update a streak for a category
     */
    @GetMapping
    public ResponseEntity<?> getStreak() {

        User currentUser = userService.getCurrentUser();

        int current = streakService.getCurrentStreak(currentUser);
        int best = streakService.getBestStreak(currentUser);

        return ResponseEntity.ok(Map.of(
            "currentStreak", current,
            "bestStreak", best
        ));
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
    
    @GetMapping("/badges")
    public ResponseEntity<List<String>> getBadges() {
        User currentUser = userService.getCurrentUser();

        int best = streakService.getBestStreak(currentUser);

        List<String> badges = new ArrayList<>();

        if (best >= 50) badges.add("50-Day Consistency Badge 🟢");
        if (best >= 100) badges.add("100-Day Consistency Badge 🔵");
        if (best >= 365) badges.add("365-Day Consistency Badge 🏆");

        return ResponseEntity.ok(badges);
    }
  

}
