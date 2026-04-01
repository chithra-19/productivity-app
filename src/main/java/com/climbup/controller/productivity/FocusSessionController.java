package com.climbup.controller.productivity;

import com.climbup.dto.request.FocusSessionRequestDTO;
import com.climbup.dto.response.FocusSessionResponseDTO;
import com.climbup.model.User;
import com.climbup.service.productivity.FocusSessionService;
import com.climbup.service.user.UserService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/focus-sessions")
public class FocusSessionController {

    private final FocusSessionService focusSessionService;
    private final UserService userService;

    public FocusSessionController(FocusSessionService focusSessionService,
                                  UserService userService) {
        this.focusSessionService = focusSessionService;
        this.userService = userService;
    }

    // ➕ Start session
    @PostMapping("/me")
    public ResponseEntity<FocusSessionResponseDTO> createSession(
            @Valid @RequestBody FocusSessionRequestDTO dto) {

        User user = userService.getCurrentUser();
        return ResponseEntity.ok(focusSessionService.startSession(dto, user));
    }

    // 📋 Get my sessions (MAIN HISTORY API)
    @GetMapping("/me")
    public ResponseEntity<Page<FocusSessionResponseDTO>> getMySessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String sortBy
    ) {

        User user = userService.getCurrentUser();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy).descending()
        );

        return ResponseEntity.ok(
                focusSessionService.getUserSessions(user, pageable)
        );
    }

    // ✅ Mark session successful (FIXED: no @AuthenticationPrincipal mismatch)
    @PostMapping("/{sessionId}/success")
    public ResponseEntity<FocusSessionResponseDTO> markSessionSuccessful(
            @PathVariable Long sessionId) {

        User user = userService.getCurrentUser();

        return ResponseEntity.ok(
                focusSessionService.markSessionSuccessful(sessionId, user)
        );
    }

    // ✏️ Update session (FIXED: removed broken userId path param)
    @PutMapping("/{sessionId}")
    public ResponseEntity<FocusSessionResponseDTO> updateSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody FocusSessionRequestDTO dto) {

        User user = userService.getCurrentUser();

        return ResponseEntity.ok(
                focusSessionService.updateSession(sessionId, dto, user)
        );
    }

    // 🗑 Delete session
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long sessionId) {

        User user = userService.getCurrentUser();

        focusSessionService.deleteSession(sessionId, user);
        return ResponseEntity.ok().build();
    }

    // 🔢 Total focus minutes
    @GetMapping("/me/total-minutes")
    public ResponseEntity<Integer> getTotalFocusMinutes() {

        User user = userService.getCurrentUser();

        return ResponseEntity.ok(
                focusSessionService.getTotalFocusMinutes(user)
        );
    }

    // 🔔 Successful sessions count
    @GetMapping("/me/successful-count")
    public ResponseEntity<Long> getSuccessfulSessionsCount() {

        User user = userService.getCurrentUser();

        return ResponseEntity.ok(
                focusSessionService.getSuccessfulSessionsCount(user)
        );
    }

    // 📅 Completed today
    @GetMapping("/me/completed-today")
    public ResponseEntity<Long> getCompletedToday() {

        User user = userService.getCurrentUser();

        return ResponseEntity.ok(
                focusSessionService.getCompletedSessionsCount(user)
        );
    }

    // 🌍 ADMIN / DEBUG ONLY - all sessions
    @GetMapping("/all")
    public ResponseEntity<Page<FocusSessionResponseDTO>> getAllSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                focusSessionService.getAllSessions(pageable)
        );
    }

    // 🟢 Complete session
    @PostMapping("/complete")
    public ResponseEntity<FocusSessionResponseDTO> completeSession() {

        User user = userService.getCurrentUser();

        return ResponseEntity.ok(
                focusSessionService.completeSession(user)
        );
    }
}