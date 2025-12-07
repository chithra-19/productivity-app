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

    // Live info
    private long elapsedMinutes;
    private long remainingMinutes;

    // Constructors
    public FocusSessionResponseDTO() {}

    public FocusSessionResponseDTO(Long id, int durationMinutes, SessionType sessionType,
                                   boolean successful, String notes, LocalDateTime startTime,
                                   LocalDateTime endTime, long elapsedMinutes, long remainingMinutes) {
        this.id = id;
        this.durationMinutes = durationMinutes;
        this.sessionType = sessionType;
        this.successful = successful;
        this.notes = notes;
        this.startTime = startTime;
        this.endTime = endTime;
        this.elapsedMinutes = elapsedMinutes;
        this.remainingMinutes = remainingMinutes;
    }

    // Factory method for mapping from entity
    public static FocusSessionResponseDTO fromEntity(com.climbup.model.FocusSession session) {
        return new FocusSessionResponseDTO(
                session.getId(),
                session.getDurationMinutes(),
                session.getSessionType(),
                session.isSuccessful(),
                session.getNotes(),
                session.getStartTime(),
                session.getEndTime(),
                session.getElapsedMinutes(),
                session.getRemainingMinutes()
        );
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

    public long getElapsedMinutes() { return elapsedMinutes; }
    public void setElapsedMinutes(long elapsedMinutes) { this.elapsedMinutes = elapsedMinutes; }

    public long getRemainingMinutes() { return remainingMinutes; }
    public void setRemainingMinutes(long remainingMinutes) { this.remainingMinutes = remainingMinutes; }
}
