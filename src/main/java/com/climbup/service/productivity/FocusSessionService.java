package com.climbup.service.productivity;

import com.climbup.dto.request.FocusSessionRequestDTO;
import com.climbup.dto.response.FocusSessionResponseDTO;
import com.climbup.model.FocusSession;
import com.climbup.model.User;
import com.climbup.repository.FocusSessionRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
        List<FocusSession> sessions = focusSessionRepository.findCurrentSessions(user);

        if (sessions.isEmpty()) {
            return null;
        }

        return sessions.get(0); // latest active session
    }
    
    @Transactional
    public FocusSessionResponseDTO startSession(FocusSessionRequestDTO dto, User user) {

        // Check if there’s already an active session
        if (focusSessionRepository.findActiveSession(user).isPresent()) {
            throw new IllegalStateException("You already have an active focus session");
        }

        FocusSession session = new FocusSession(
            dto.getDurationMinutes(),
            dto.getSessionType(),
            user
        );

        session.startSession();
        focusSessionRepository.save(session);

        return mapToResponse(session);
    }

    
    @Transactional
    public FocusSessionResponseDTO updateSession(Long sessionId, FocusSessionRequestDTO dto, User user) {
        FocusSession session = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.getUser().equals(user)) {
            throw new IllegalStateException("You cannot update another user's session");
        }

        // Only allow update if session is not completed
        if (session.getEndTime() != null) {
            throw new IllegalStateException("Cannot update a completed session");
        }

        // Update allowed fields
        session.setDurationMinutes(dto.getDurationMinutes());
        session.setNotes(dto.getNotes());
        session.setSessionType(dto.getSessionType());

        focusSessionRepository.save(session);
        return mapToResponse(session);
    }


    @Transactional
    public FocusSessionResponseDTO completeSession(User user) {

        // Get active session or throw if none
        FocusSession session = focusSessionRepository.findActiveSession(user)
                .orElseThrow(() -> new IllegalStateException("No active focus session"));

        // Complete the session
        session.completeSession();

        // Save changes
        focusSessionRepository.save(session);

        return mapToResponse(session);
    }



    // Compute remaining time for current session in minutes
    public long getRemainingMinutes(FocusSession session) {
        if (session == null) return 0;
        LocalDateTime endTime = session.getStartTime().plusMinutes(session.getDurationMinutes());
        long remaining = java.time.Duration.between(LocalDateTime.now(), endTime).toMinutes();
        return remaining > 0 ? remaining : 0;
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


    @Transactional
    public FocusSessionResponseDTO markSessionSuccessful(Long sessionId, User user) {
        FocusSession session = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getUser().equals(user)) {
            throw new IllegalStateException("You cannot update another user's session");
        }

        session.setSuccessful(true);
        focusSessionRepository.save(session);
        return mapToResponse(session);
    }

    @Transactional
    public void deleteSession(Long sessionId, User user) {
        FocusSession session = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getUser().equals(user)) {
            throw new IllegalStateException("You cannot delete another user's session");
        }

        focusSessionRepository.delete(session);
    }
    
    public List<FocusSessionResponseDTO> getUserSessions(User user) {
        return focusSessionRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


}
