package com.climbup.service.productivity;

import com.climbup.dto.request.FocusSessionRequestDTO;
import com.climbup.dto.response.FocusSessionResponseDTO;
import com.climbup.model.FocusSession;
import com.climbup.model.SessionStatus;
import com.climbup.model.SessionType;
import com.climbup.model.User;
import com.climbup.repository.FocusSessionRepository;
import com.climbup.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.Duration;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;


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
    	OffsetDateTime todayStart = OffsetDateTime.now()
    	        .withHour(0)
    	        .withMinute(0)
    	        .withSecond(0)
    	        .withNano(0);
        return focusSessionRepository
                .findByUserIdAndStartTimeAfter(user.getId(), todayStart);
    }

    // Count successful sessions today
    public Long getCompletedSessionsCount(User user) {
    	OffsetDateTime todayStart = OffsetDateTime.now()
    	        .withHour(0)
    	        .withMinute(0)
    	        .withSecond(0)
    	        .withNano(0);

    	return focusSessionRepository
    	        .countByUserIdAndStatusAndStartTimeAfter(
    	                user.getId(),
    	                SessionStatus.COMPLETED,
    	                todayStart
    	        );
    }
    public FocusSession getCurrentSession(User user) {
        return focusSessionRepository
                .findTopByUserIdAndStatusOrderByStartTimeDesc(user.getId(), SessionStatus.ACTIVE)
                .orElse(null);
    }
    
    @Transactional
    public FocusSessionResponseDTO startSession(FocusSessionRequestDTO dto, User user) {

        Optional<FocusSession> activeOpt =
                focusSessionRepository.findTopByUserIdAndStatusOrderByStartTimeDesc(
                        user.getId(),
                        SessionStatus.ACTIVE
                );

        if (activeOpt.isPresent()) {

            FocusSession active = activeOpt.get();

            OffsetDateTime plannedEnd =
                    active.getStartTime().plusMinutes(active.getDurationMinutes());

            OffsetDateTime now = OffsetDateTime.now();

            // ✅ auto-complete expired session
            if (now.isAfter(plannedEnd)) {

                active.setStatus(SessionStatus.COMPLETED);
                active.setEndTime(plannedEnd); // 🔥 important: use planned end, not now

                focusSessionRepository.save(active);

            } else {
                throw new IllegalStateException("You already have an active focus session");
            }
        }

        FocusSession session = new FocusSession(
                dto.getDurationMinutes(),
                dto.getSessionType(),
                user
        );

        session.setStartTime(OffsetDateTime.now());
        session.setStatus(SessionStatus.ACTIVE);
        session.setEndTime(null);

        focusSessionRepository.save(session);

        return mapToResponse(session);
    }

    @Transactional
    public FocusSessionResponseDTO endSession(Long sessionId) {

        FocusSession session = focusSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getEndTime() != null) {
            return mapToResponse(session); // already completed
        }

        session.setEndTime(OffsetDateTime.now());
        session.setStatus(SessionStatus.COMPLETED);

        long elapsed = Duration
                .between(session.getStartTime(), session.getEndTime())
                .toMinutes();

        session.setElapsedMinutes((int) Math.max(elapsed, 0));
        focusSessionRepository.save(session);

        return mapToResponse(session);
    }
    
    public Page<FocusSessionResponseDTO> getUserSessionsByStatus(
            User user,
            SessionStatus status,
            Pageable pageable) {

        return focusSessionRepository
                .findByUserIdAndStatus(user.getId(), status, pageable)
                .map(this::mapToResponse);
    }
    
    @Transactional
    public FocusSessionResponseDTO updateSession(Long sessionId, FocusSessionRequestDTO dto, User user) {
        FocusSession session = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You cannot update another user's session");
        }

        // Only allow update if session is not completed
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new IllegalStateException("Cannot update a completed session");
        }

        // Update allowed fields
        session.setDurationMinutes(dto.getDurationMinutes());
        session.setNotes(dto.getNotes());
        session.setSessionType(dto.getSessionType());

        focusSessionRepository.save(session);
        return mapToResponse(session);
    }


    // Compute remaining time for current session in minutes
    public long getRemainingMinutes(FocusSession session) {
        if (session == null) return 0;

        OffsetDateTime endTime =
                session.getStartTime().plusMinutes(session.getDurationMinutes());

        long remaining = Duration
                .between(OffsetDateTime.now(), endTime)
                .toMinutes();

        return Math.max(remaining, 0);
    }


    // Get total focus minutes for a user
    public int getTotalFocusMinutes(User user) {
        return focusSessionRepository.sumElapsedMinutesByUserId(user.getId());
    }
    
    // Get count of successful sessions
    public long getSuccessfulSessionsCount(User user) {
        return focusSessionRepository
                .countByUserIdAndStatus(user.getId(), SessionStatus.COMPLETED);
    }
    private FocusSessionResponseDTO mapToResponse(FocusSession session) {

        FocusSessionResponseDTO dto = new FocusSessionResponseDTO();

        dto.setId(session.getId());
        dto.setDurationMinutes(session.getDurationMinutes());
        dto.setSessionType(session.getSessionType());
        dto.setStatus(session.getStatus());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setNotes(session.getNotes());

        OffsetDateTime now = OffsetDateTime.now();

        long elapsedMinutes = 0;
        long remainingMinutes = 0;

        // ✅ ELAPSED TIME
        if (session.getStartTime() != null) {

            OffsetDateTime effectiveEnd;

            if (session.getStatus() == SessionStatus.ACTIVE) {

                OffsetDateTime plannedEnd =
                        session.getStartTime().plusMinutes(session.getDurationMinutes());

                // ⏱ cap elapsed time at planned end
                effectiveEnd = now.isAfter(plannedEnd) ? plannedEnd : now;

            } else {
                // completed / aborted
                effectiveEnd = session.getEndTime();
            }

            if (effectiveEnd != null) {
                elapsedMinutes = Duration
                        .between(session.getStartTime(), effectiveEnd)
                        .toMinutes();
            }
        }

        // ✅ REMAINING TIME (ONLY IF ACTIVE)
        if (session.getStatus() == SessionStatus.ACTIVE
                && session.getStartTime() != null) {

            OffsetDateTime plannedEnd =
                    session.getStartTime().plusMinutes(session.getDurationMinutes());

            remainingMinutes = Duration
                    .between(now, plannedEnd)
                    .toMinutes();
        }

        dto.setElapsedMinutes(Math.max(elapsedMinutes, 0));
        dto.setRemainingMinutes(Math.max(remainingMinutes, 0));

        return dto;
    }
    
    public List<FocusSessionResponseDTO> getAllUserSessions(User user) {
        return focusSessionRepository
                .findByUserIdOrderByStartTimeDesc(user.getId())
                .stream()
                .map(this::mapToResponse)   // ✅ FIXED
                .toList();
    }
    @Scheduled(fixedDelay = 60000, initialDelay = 120000)
    @Transactional
    public void autoCompleteExpiredSessions() {

        Page<FocusSession> page =
            focusSessionRepository.findByStatus(
                SessionStatus.ACTIVE,
                PageRequest.of(0, 50)
            );

        List<FocusSession> activeSessions = page.getContent(); // ✅ IMPORTANT

        OffsetDateTime now = OffsetDateTime.now();

        for (FocusSession session : activeSessions) {

            if (session.getStartTime() == null) continue;

            OffsetDateTime plannedEnd =
                    session.getStartTime().plusMinutes(session.getDurationMinutes());

            if (now.isAfter(plannedEnd)) {

                session.setStatus(SessionStatus.COMPLETED);
                session.setEndTime(plannedEnd);
                session.setElapsedMinutes(session.getDurationMinutes());

                focusSessionRepository.save(session);
            }
        }
    }
    @Transactional
    public FocusSessionResponseDTO markSessionSuccessful(Long sessionId, User user) {

        FocusSession session = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Unauthorized");
        }

        if (session.getStatus() == SessionStatus.COMPLETED) {
            return mapToResponse(session);
        }

        session.completeSession();

        OffsetDateTime now = OffsetDateTime.now();

        long elapsedMinutes = 0;

        if (session.getStartTime() != null) {

            OffsetDateTime plannedEnd =
                    session.getStartTime().plusMinutes(session.getDurationMinutes());

            OffsetDateTime effectiveEnd =
                    now.isAfter(plannedEnd) ? plannedEnd : now;

            elapsedMinutes = Duration
                    .between(session.getStartTime(), effectiveEnd)
                    .toMinutes();
        }

        // ✅ save session
        focusSessionRepository.save(session);

        // 🔥 ONLY count focus/custom sessions
        if (shouldCountFocus(session)) {
            user.addFocusMinutes((int) Math.max(elapsedMinutes, 0));
            userRepository.save(user);
        }

        return mapToResponse(session);
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
@Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Kolkata")
@Transactional
public void resetDailyFocusMinutes() {
    try {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            user.setDailyGoalMinutes(0);
        }

        userRepository.saveAll(users); // 🔥 better than save inside loop
    } catch (Exception e) {
        e.printStackTrace(); // prevent crash
    }
}
@Transactional
public FocusSessionResponseDTO abortSession(User user) {

    FocusSession session = focusSessionRepository
            .findTopByUserIdAndStatusOrderByStartTimeDesc(
                    user.getId(),
                    SessionStatus.ACTIVE
            )
            .orElseThrow(() -> new IllegalStateException("No active session"));

    OffsetDateTime now = OffsetDateTime.now();

    long elapsedMinutes = Duration
            .between(session.getStartTime(), now)
            .toMinutes();

    // 🔴 CASE 1: Less than 1 min → IGNORE
    if (elapsedMinutes < 1) {
        session.setEndTime(now);
        session.setElapsedMinutes(0);
        session.setStatus(SessionStatus.ABORTED);

        focusSessionRepository.save(session);
        return mapToResponse(session);
    }

    // 🟢 CASE 2: Valid session (>= 1 min)
    session.setEndTime(now);
    session.setElapsedMinutes((int) elapsedMinutes);
    session.setStatus(SessionStatus.ABORTED);

    // ✅ Add to goal
    if (shouldCountFocus(session)) {
        user.addFocusMinutes((int) elapsedMinutes);
        userRepository.save(user);
    }

    focusSessionRepository.save(session);

    return mapToResponse(session);
}
public List<FocusSessionResponseDTO> getSessionsByStatus(User user, SessionStatus status) {
    return focusSessionRepository
            .findByUserIdAndStatusOrderByStartTimeDesc(user.getId(), status)
            .stream()
            .map(this::mapToResponse)
            .toList();
}


private boolean shouldCountFocus(FocusSession session) {
    if (session.getSessionType() == null) return false; // 🔥 FIX

    return session.getSessionType() == FocusSession.SessionType.FOCUS
        || session.getSessionType() == FocusSession.SessionType.CUSTOM;
}
}
