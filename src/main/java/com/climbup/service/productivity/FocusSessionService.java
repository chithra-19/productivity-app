package com.climbup.service.productivity;

import com.climbup.dto.request.FocusSessionRequestDTO;
import com.climbup.dto.response.FocusSessionResponseDTO;
import com.climbup.model.FocusSession;
import com.climbup.model.User;
import com.climbup.repository.FocusSessionRepository;
import com.climbup.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;

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
    private final UserRepository userRepository;
    

    public FocusSessionService(FocusSessionRepository focusSessionRepository,
    		UserRepository userRepository) {
        this.focusSessionRepository = focusSessionRepository;
        this.userRepository = userRepository;
    }

    // Fetch all sessions for today
    public List<FocusSession> getTodaySessions(User user) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        return focusSessionRepository.findTodaySessions(user.getId() ,todayStart);
    }

    // Count successful sessions today
    public Long getCompletedSessionsCount(User user) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        return focusSessionRepository.countCompletedToday(user.getId(), todayStart);
    }

    // Get the current active session (endTime == null)
    public FocusSession getCurrentSession(User user) {
        return focusSessionRepository
                .findTopByUserIdAndEndTimeIsNullOrderByStartTimeDesc(user.getId())
                .orElse(null);
    }
    
    @Transactional
    public FocusSessionResponseDTO startSession(FocusSessionRequestDTO dto, User user) {

        // Check if there’s already an active session
        if (focusSessionRepository.findActiveSession(user.getId()).isPresent()) {
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

        if (!session.getUser().getId().equals(user.getId())) {
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
        FocusSession session = focusSessionRepository.findActiveSession(user.getId())
                .orElseThrow(() -> new IllegalStateException("No active focus session"));

        // Complete the session
        session.completeSession();
        focusSessionRepository.save(session);

        // ✅ Add session minutes to daily goal progress
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow();

        managedUser.addFocusMinutes(session.getDurationMinutes());
        userRepository.save(managedUser);
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
        return focusSessionRepository
                .findByUserIdAndSuccessfulTrue(user.getId())
                .stream()
                .mapToInt(FocusSession::getDurationMinutes)
                .sum();
    }

    // Get count of successful sessions
    public long getSuccessfulSessionsCount(User user) {
        return focusSessionRepository.countByUserIdAndSuccessfulTrue(user.getId());
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


    public FocusSessionResponseDTO markSessionSuccessful(Long sessionId, User user) {

        FocusSession session = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // ownership check
        if (!session.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Unauthorized");
        }

        // mark session complete
        session.setSuccessful(true);
        session.setEndTime(LocalDateTime.now());
        focusSessionRepository.save(session);

        // update user progress (NO DB FETCH NEEDED)
        user.addFocusMinutes(session.getDurationMinutes());
        userRepository.save(user);

        return FocusSessionResponseDTO.fromEntity(session);
    }

    @Transactional
    public void deleteSession(Long sessionId, User user) {
        FocusSession session = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You cannot delete another user's session");
        }

        focusSessionRepository.delete(session);
    }
    public Page<FocusSessionResponseDTO> getAllSessions(Pageable pageable) {
        return focusSessionRepository.findAll(pageable)
                .map(this::mapToResponse);
    }
    

public Page<FocusSessionResponseDTO> getUserSessions(User user, Pageable pageable) {
    return focusSessionRepository.findByUserId(user.getId(), pageable)
                                 .map(this::mapToResponse);


}
//✅ Reset daily focus minutes at midnight
@Scheduled(cron = "0 0 0 * * ?")
@Transactional
public void resetDailyFocusMinutes() {
    List<User> users = userRepository.findAll();
    for (User user : users) {
        user.setDailyGoalMinutes(0);
        userRepository.save(user);
    }
}

@Transactional
public FocusSessionResponseDTO abortSession(User user) {

    FocusSession session = focusSessionRepository.findActiveSession(user.getId())
            .orElseThrow(() -> new IllegalStateException("No active session"));

    session.setSuccessful(false); // explicitly aborted
    session.setEndTime(LocalDateTime.now());

    focusSessionRepository.save(session);

    return mapToResponse(session);
}



}
