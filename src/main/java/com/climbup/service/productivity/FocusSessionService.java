package com.climbup.service.productivity;

import com.climbup.dto.request.FocusSessionRequestDTO;
import com.climbup.dto.response.FocusSessionResponseDTO;
import com.climbup.mapper.FocusSessionMapper;
import com.climbup.model.FocusSession;
import com.climbup.model.User;
import com.climbup.repository.FocusSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FocusSessionService {

    private final FocusSessionRepository focusSessionRepository;

    @Autowired
    public FocusSessionService(FocusSessionRepository focusSessionRepository) {
        this.focusSessionRepository = focusSessionRepository;
    }

    // ➕ Create a new focus session
    public FocusSessionResponseDTO createSession(FocusSessionRequestDTO dto, User user) {
        FocusSession session = FocusSessionMapper.toEntity(dto, user);
        FocusSession saved = focusSessionRepository.save(session);
        return FocusSessionMapper.toResponse(saved);
    }

    // 📋 Get all focus sessions for a user
    public List<FocusSessionResponseDTO> getUserSessions(User user) {
        return focusSessionRepository.findByUser(user).stream()
                .map(FocusSessionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ✅ Mark a session as successful
    @Transactional
    public FocusSessionResponseDTO markSessionSuccessful(Long sessionId, User user) {
        FocusSession session = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Focus session not found"));

        if (!session.getUser().equals(user)) {
            throw new SecurityException("You can only update your own sessions");
        }

        session.markSuccessful();
        return FocusSessionMapper.toResponse(session);
    }

    // ✏️ Update an existing session
    @Transactional
    public FocusSessionResponseDTO updateSession(Long sessionId, FocusSessionRequestDTO dto, User user) {
        FocusSession session = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Focus session not found"));

        if (!session.getUser().equals(user)) {
            throw new SecurityException("You can only update your own sessions");
        }

        FocusSessionMapper.updateEntity(session, dto);
        FocusSession updated = focusSessionRepository.save(session);
        return FocusSessionMapper.toResponse(updated);
    }

    // ❌ Delete a session
    public void deleteSession(Long sessionId, User user) {
        FocusSession session = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Focus session not found"));

        if (!session.getUser().equals(user)) {
            throw new SecurityException("You can only delete your own sessions");
        }

        focusSessionRepository.delete(session);
    }

    // 🔢 Get total duration of all sessions for a user
    public int getTotalFocusMinutes(User user) {
        return focusSessionRepository.findByUser(user).stream()
                .mapToInt(FocusSession::getDurationMinutes)
                .sum();
    }

    // 🔔 Get completed/successful sessions count
    public long getSuccessfulSessionsCount(User user) {
        return focusSessionRepository.findByUser(user).stream()
                .filter(FocusSession::isSuccessful)
                .count();
    }
}
