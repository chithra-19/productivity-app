package com.climbup.dto.request;

import com.climbup.model.FocusSession.SessionType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;


public class FocusSessionRequestDTO {

    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 180, message = "Duration cannot exceed 180 minutes")
    private int durationMinutes;

    private SessionType sessionType = SessionType.POMODORO;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    // Constructors
    public FocusSessionRequestDTO() {}

    public FocusSessionRequestDTO(int durationMinutes, SessionType sessionType, String notes) {
        this.durationMinutes = durationMinutes;
        this.sessionType = sessionType;
        this.notes = notes;
    }

    // Getters & Setters
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public SessionType getSessionType() { return sessionType; }
    public void setSessionType(SessionType sessionType) { this.sessionType = sessionType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
