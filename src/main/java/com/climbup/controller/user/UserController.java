package com.climbup.controller.user;

import com.climbup.dto.request.UserRequestDTO;
import com.climbup.dto.response.DailyProgressDTO;
import com.climbup.dto.response.DailyStatsDTO;
import com.climbup.dto.response.UserResponseDTO;
import com.climbup.dto.response.UserStatsDTO;
import com.climbup.mapper.UserMapper;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.UserRepository;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.productivity.FocusSessionService;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.user.UserService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AchievementService achievementService;
    private final StreakTrackerService streakTrackerService;
    private final UserRepository userRepository;
    private final FocusSessionService focusSessionService;
    
    @Autowired
    public UserController(AchievementService achievementService, 
    		StreakTrackerService streakTrackerService,
    		UserService userService,
    		UserRepository userRepository,
    		FocusSessionService focusSessionService) {
        this.achievementService = achievementService;
        this.userService = userService;
        this.streakTrackerService = streakTrackerService;
        this.userRepository = userRepository;
        this.focusSessionService = focusSessionService;
        }
    

    // ---------- Register new user ----------
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO dto) {
        User createdUser = userService.registerUser(dto);
        return ResponseEntity.ok(UserMapper.toResponseDTO(createdUser));
    }

    // ---------- Get logged-in user profile ----------
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return ResponseEntity.ok(UserMapper.toResponseDTO(user));
    }

    // ---------- Get all users ----------
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers()
                .stream()
                .map(UserMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(users);
    }

    // ---------- Update user profile ----------
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO dto,
            Principal principal) {

        UserResponseDTO updated = userService.updateUser(id, dto, principal.getName());
        return ResponseEntity.ok(updated);
    }

    // ---------- Delete user ----------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/stats")
    public ResponseEntity<UserStatsDTO> getStats(Principal principal) {
        User user = userService.findByEmail(principal.getName());

        int currentStreak = streakTrackerService.getCurrentStreak(user);
        int bestStreak = streakTrackerService.getBestStreak(user.getId());

        return ResponseEntity.ok(
            new UserStatsDTO(
                currentStreak,
                bestStreak
            )
        );

    }
    
    @PutMapping("/{id}/update-daily-goal")
    public ResponseEntity<Void> updateDailyGoal(@PathVariable Long id, @RequestParam int goalMinutes) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setDailyGoalMinutes(goalMinutes);
        userRepository.save(user);

        return 
        		ResponseEntity.ok().build();
    }


    @GetMapping("/user/{userId}/daily-stats")
    public ResponseEntity<DailyStatsDTO> getDailyStats(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        int focusMinutes = user.getDailyGoalMinutes();
        int goalMinutes = user.getDailyGoalMinutes();
        long sessionCount = focusSessionService.getCompletedSessionsCount(user);

        DailyStatsDTO stats = new DailyStatsDTO(focusMinutes, goalMinutes, sessionCount);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/{id}/daily-progress")
    public DailyProgressDTO getDailyProgress(@PathVariable Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return new DailyProgressDTO(
            user.getTotalFocusMinutes(),
            user.getDailyGoalMinutes()
        );
    }

}
