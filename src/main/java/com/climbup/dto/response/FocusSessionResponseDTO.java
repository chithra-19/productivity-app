package com.climbup.dto.response;

import com.climbup.model.FocusSession.SessionType;
import com.climbup.model.SessionStatus;

import java.time.OffsetDateTime;

public class FocusSessionResponseDTO {

    private Long id;
    private Long userId;

    private int durationMinutes;
    private SessionType sessionType;
    private SessionStatus status;

    private String notes;

    // ✅ USE OffsetDateTime (matches backend)
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;

    private long elapsedMinutes;
    private long remainingMinutes;

    private boolean active;

    public FocusSessionResponseDTO() {}

    // ✅ FIXED CONSTRUCTOR
    public FocusSessionResponseDTO(Long id,
                                   Long userId,
                                   int durationMinutes,
                                   SessionType sessionType,
                                   SessionStatus status,
                                   String notes,
                                   OffsetDateTime startTime,
                                   OffsetDateTime endTime,
                                   long elapsedMinutes,
                                   long remainingMinutes,
                                   boolean active) {
        this.id = id;
        this.userId = userId;
        this.durationMinutes = durationMinutes;
        this.sessionType = sessionType;
        this.status = status;
        this.notes = notes;
        this.startTime = startTime;
        this.endTime = endTime;
        this.elapsedMinutes = elapsedMinutes;
        this.remainingMinutes = remainingMinutes;
        this.active = active;
    }

    // ================= GETTERS & SETTERS =================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }

    public long getElapsedMinutes() {
        return elapsedMinutes;
    }

    public void setElapsedMinutes(long elapsedMinutes) {
        this.elapsedMinutes = elapsedMinutes;
    }

    public long getRemainingMinutes() {
        return remainingMinutes;
    }

    public void setRemainingMinutes(long remainingMinutes) {
        this.remainingMinutes = remainingMinutes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}