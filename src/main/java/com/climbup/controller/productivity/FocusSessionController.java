package com.climbup.controller.productivity;

import com.climbup.dto.request.FocusSessionRequestDTO;
import com.climbup.dto.response.FocusSessionResponseDTO;
import com.climbup.model.User;
import com.climbup.repository.UserRepository;
import com.climbup.service.productivity.FocusSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/focus-sessions")
public class FocusSessionController {

    private final FocusSessionService focusSessionService;
    private final UserRepository userRepository;

    public FocusSessionController(FocusSessionService focusSessionService, UserRepository userRepository) {
        this.focusSessionService = focusSessionService;
        this.userRepository = userRepository;
    }

    // ➕ Create a new focus session
    @PostMapping("/create/{userId}")
    public ResponseEntity<FocusSessionResponseDTO> createSession(
            @PathVariable Long userId,
            @Valid @RequestBody FocusSessionRequestDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FocusSessionResponseDTO response = focusSessionService.createSession(dto, user);
        return ResponseEntity.ok(response);
    }

    // 📋 Get all focus sessions for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FocusSessionResponseDTO>> getUserSessions(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<FocusSessionResponseDTO> sessions = focusSessionService.getUserSessions(user);
        return ResponseEntity.ok(sessions);
    }

    // ✅ Mark a session as successful
    @PostMapping("/{sessionId}/mark-successful/{userId}")
    public ResponseEntity<FocusSessionResponseDTO> markSessionSuccessful(
            @PathVariable Long sessionId,
            @PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FocusSessionResponseDTO response = focusSessionService.markSessionSuccessful(sessionId, user);
        return ResponseEntity.ok(response);
    }

    // ✏️ Update an existing session
    @PutMapping("/{sessionId}/update/{userId}")
    public ResponseEntity<FocusSessionResponseDTO> updateSession(
            @PathVariable Long sessionId,
            @PathVariable Long userId,
            @Valid @RequestBody FocusSessionRequestDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FocusSessionResponseDTO updated = focusSessionService.updateSession(sessionId, dto, user);
        return ResponseEntity.ok(updated);
    }

    // ❌ Delete a session
    @DeleteMapping("/{sessionId}/delete/{userId}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long sessionId,
            @PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        focusSessionService.deleteSession(sessionId, user);
        return ResponseEntity.ok().build();
    }

    // 🔢 Get total focus minutes for a user
    @GetMapping("/user/{userId}/total-minutes")
    public ResponseEntity<Integer> getTotalFocusMinutes(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int totalMinutes = focusSessionService.getTotalFocusMinutes(user);
        return ResponseEntity.ok(totalMinutes);
    }

    // 🔔 Get count of successful sessions
    @GetMapping("/user/{userId}/successful-count")
    public ResponseEntity<Long> getSuccessfulSessionsCount(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long count = focusSessionService.getSuccessfulSessionsCount(user);
        return ResponseEntity.ok(count);
    }
}
