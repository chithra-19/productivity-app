package com.climbup.dto.response;

import com.climbup.model.FocusSession.SessionType;

import java.time.LocalDateTime;

public class FocusSessionResponseDTO {

    private Long id;
    private int durationMinutes;
    private SessionType sessionType;
    private boolean successful;
    private String notes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long userId;

    // Constructors
    public FocusSessionResponseDTO() {}

    public FocusSessionResponseDTO(Long id, int durationMinutes, SessionType sessionType, boolean successful,
                                   String notes, LocalDateTime startTime, LocalDateTime endTime, Long userId) {
        this.id = id;
        this.durationMinutes = durationMinutes;
        this.sessionType = sessionType;
        this.successful = successful;
        this.notes = notes;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userId = userId;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public SessionType getSessionType() { return sessionType; }
    public void setSessionType(SessionType sessionType) { this.sessionType = sessionType; }

    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
