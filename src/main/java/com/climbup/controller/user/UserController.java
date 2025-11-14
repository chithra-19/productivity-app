package com.climbup.controller.user;

import com.climbup.dto.request.UserRequestDTO;
import com.climbup.dto.response.UserResponseDTO;
import com.climbup.dto.response.UserStatsDTO;
import com.climbup.mapper.UserMapper;
import com.climbup.model.User;
import com.climbup.service.productivity.AchievementService;
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

    @Autowired
    public UserController(AchievementService achievementService, UserService userService) {
        this.achievementService = achievementService;
        this.userService = userService;
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
        User user = userService.findByUsername(principal.getName());
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
        User user = userService.findByUsername(principal.getName());
        int streak = user.getCurrentStreak();
        int best = user.getBestStreak();
        int productivity = achievementService.getProductivityScore(user);
        return ResponseEntity.ok(new UserStatsDTO(streak, best, productivity));
    }
}
