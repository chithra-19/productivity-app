package com.climbup.mapper;

import com.climbup.dto.request.FocusSessionRequestDTO;
import com.climbup.dto.response.FocusSessionResponseDTO;
import com.climbup.model.FocusSession;
import com.climbup.model.User;

public class FocusSessionMapper {

    // 🔹 RequestDTO → Entity
    public static FocusSession toEntity(FocusSessionRequestDTO dto, User user) {
        FocusSession session = new FocusSession();
        session.setDurationMinutes(dto.getDurationMinutes());
        session.setSessionType(dto.getSessionType() != null ? dto.getSessionType() : FocusSession.SessionType.POMODORO);
        session.setNotes(dto.getNotes());
        session.setUser(user);
        return session;
    }

    // 🔹 Entity → ResponseDTO
    public static FocusSessionResponseDTO toResponse(FocusSession session) {
        FocusSessionResponseDTO dto = new FocusSessionResponseDTO();
        dto.setId(session.getId());
        dto.setDurationMinutes(session.getDurationMinutes());
        dto.setSessionType(session.getSessionType());
        dto.setSuccessful(session.isSuccessful());
        dto.setNotes(session.getNotes());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setUserId(session.getUser() != null ? session.getUser().getId() : null);
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
