package com.climbup.mapper;

import com.climbup.dto.request.FocusSessionRequestDTO;
import com.climbup.dto.response.FocusSessionResponseDTO;
import com.climbup.model.FocusSession;
import com.climbup.model.SessionStatus;
import com.climbup.model.User;

import java.time.Duration;
import java.time.OffsetDateTime;

public class FocusSessionMapper {

    // 🔹 RequestDTO → Entity
    public static FocusSession toEntity(FocusSessionRequestDTO dto, User user) {
        FocusSession session = new FocusSession();

        session.setDurationMinutes(dto.getDurationMinutes());

        session.setSessionType(
                dto.getSessionType() != null
                        ? dto.getSessionType()
                        : FocusSession.SessionType.FOCUS
        );

        session.setNotes(dto.getNotes());
        session.setUser(user);

        return session;
    }

    // 🔹 Entity → ResponseDTO
    public static FocusSessionResponseDTO toResponse(FocusSession session) {

        FocusSessionResponseDTO dto = new FocusSessionResponseDTO();

        // ✅ BASIC FIELDS
        dto.setId(session.getId());
        dto.setUserId(session.getUser().getId());
        dto.setDurationMinutes(session.getDurationMinutes());
        dto.setSessionType(session.getSessionType());
        dto.setStatus(session.getStatus());
        dto.setNotes(session.getNotes());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());

        // 🔥 TIME CALCULATIONS (CORRECT + SAFE)
        OffsetDateTime now = OffsetDateTime.now();

        long total = session.getDurationMinutes();
        long elapsed = 0;
        long remaining = 0;

        if (session.getStartTime() != null) {

            OffsetDateTime plannedEnd =
                    session.getStartTime().plusMinutes(total);

            OffsetDateTime effectiveEnd;

            if (session.getStatus() == SessionStatus.ACTIVE) {
                // ⏱ cap at planned end
                effectiveEnd = now.isAfter(plannedEnd) ? plannedEnd : now;
            } else {
                // completed / aborted
                effectiveEnd = session.getEndTime();
            }

            if (effectiveEnd != null) {
                elapsed = Duration
                        .between(session.getStartTime(), effectiveEnd)
                        .toMinutes();
            }

            // remaining only for ACTIVE sessions
            if (session.getStatus() == SessionStatus.ACTIVE) {
                remaining = Duration
                        .between(now, plannedEnd)
                        .toMinutes();
            }
        }

        dto.setElapsedMinutes(Math.max(elapsed, 0));
        dto.setRemainingMinutes(Math.max(remaining, 0));

        // ✅ ACTIVE FLAG
        dto.setActive(session.getStatus() == SessionStatus.ACTIVE);

        return dto;
    }

    // 🔹 Update existing entity from DTO
    public static void updateEntity(FocusSession session, FocusSessionRequestDTO dto) {

        if (dto.getDurationMinutes() > 0) {
            session.setDurationMinutes(dto.getDurationMinutes());
        }

        if (dto.getSessionType() != null) {
            session.setSessionType(dto.getSessionType());
        }

        if (dto.getNotes() != null) {
            session.setNotes(dto.getNotes());
        }
    }
}