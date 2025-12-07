package com.climbup.service.productivity;

import com.climbup.dto.request.FocusSessionRequestDTO;
import com.climbup.dto.response.FocusSessionResponseDTO;
import com.climbup.model.FocusSession;
import com.climbup.model.User;
import com.climbup.repository.FocusSessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FocusSessionService {

    private final FocusSessionRepository focusSessionRepository;

    public FocusSessionService(FocusSessionRepository focusSessionRepository) {
        this.focusSessionRepository = focusSessionRepository;
    }

    // Fetch all sessions for today
    public List<FocusSession> getTodaySessions(User user) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        return focusSessionRepository.findTodaySessions(user, todayStart);
    }

    // Count successful sessions today
    public Long getCompletedSessionsCount(User user) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        return focusSessionRepository.countCompletedToday(user, todayStart);
    }

    // Get the current active session (endTime == null)
    public FocusSession getCurrentSession(User user) {
        return focusSessionRepository.findCurrentSession(user);
    }

    // Compute remaining time for current session in minutes
    public long getRemainingMinutes(FocusSession session) {
        if (session == null) return 0;
        LocalDateTime endTime = session.getStartTime().plusMinutes(session.getDurationMinutes());
        long remaining = java.time.Duration.between(LocalDateTime.now(), endTime).toMinutes();
        return remaining > 0 ? remaining : 0;
    }

    // Create a new focus session
    public FocusSessionResponseDTO createSession(FocusSessionRequestDTO dto, User user) {
        FocusSession session = new FocusSession();
        session.setUser(user);
        session.setDurationMinutes(dto.getDurationMinutes() > 0 ? dto.getDurationMinutes() : 25); // default 25
        session.setSessionType(dto.getSessionType() != null ? dto.getSessionType() : FocusSession.SessionType.POMODORO);
        session.setStartTime(LocalDateTime.now());
        session.setSuccessful(false);
        session.setNotes(dto.getNotes());

        FocusSession saved = focusSessionRepository.save(session);
        return mapToResponse(saved);
    }

    // Mark a session as successful
    public FocusSessionResponseDTO markSessionSuccessful(Long sessionId, User user) {
        FocusSession session = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Focus session not found"));

        if (!session.getUser().equals(user)) {
            throw new RuntimeException("You cannot update another user's session");
        }

        session.setSuccessful(true);
        session.setEndTime(LocalDateTime.now());
        FocusSession updated = focusSessionRepository.save(session);

        return mapToResponse(updated);
    }

    // Update an existing session (optional)
    public FocusSessionResponseDTO updateSession(Long sessionId, FocusSessionRequestDTO dto, User user) {
        FocusSession session = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Focus session not found"));

        if (!session.getUser().equals(user)) {
            throw new RuntimeException("You cannot update another user's session");
        }

        if (dto.getDurationMinutes() > 0) session.setDurationMinutes(dto.getDurationMinutes());
        if (dto.getSessionType() != null) session.setSessionType(dto.getSessionType());
        session.setNotes(dto.getNotes());

        FocusSession updated = focusSessionRepository.save(session);
        return mapToResponse(updated);
    }

    // Delete a session
    public void deleteSession(Long sessionId, User user) {
        FocusSession session = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Focus session not found"));

        if (!session.getUser().equals(user)) {
            throw new RuntimeException("You cannot delete another user's session");
        }

        focusSessionRepository.delete(session);
    }

    // Get total focus minutes for a user
    public int getTotalFocusMinutes(User user) {
        List<FocusSession> sessions = focusSessionRepository.findAll()
                .stream()
                .filter(s -> s.getUser().equals(user) && s.isSuccessful())
                .collect(Collectors.toList());

        return sessions.stream().mapToInt(FocusSession::getDurationMinutes).sum();
    }

    // Get count of successful sessions
    public long getSuccessfulSessionsCount(User user) {
        return focusSessionRepository.findAll()
                .stream()
                .filter(s -> s.getUser().equals(user) && s.isSuccessful())
                .count();
    }

    // Utility: map FocusSession → Response DTO
    private FocusSessionResponseDTO mapToResponse(FocusSession session) {
        FocusSessionResponseDTO dto = new FocusSessionResponseDTO();
        dto.setId(session.getId());
        dto.setDurationMinutes(session.getDurationMinutes());
        dto.setSessionType(session.getSessionType());
        dto.setSuccessful(session.isSuccessful());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setNotes(session.getNotes());

        // Add live info
        dto.setElapsedMinutes(session.getElapsedMinutes());
        dto.setRemainingMinutes(session.getRemainingMinutes());

        return dto;
    }


    public List<FocusSessionResponseDTO> getUserSessions(User user) {
        return focusSessionRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
