package com.climbup.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "focus_sessions")
public class FocusSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    @Max(180)
    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes = 25;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType sessionType = SessionType.POMODORO;

    @Column(name = "successful")
    private boolean successful = false;

    @Column(name = "notes")
    private String notes;

    
    @Column(name = "start_time", nullable = true, updatable = false)
    private LocalDateTime startTime;


    @Column(name = "end_time")
    private LocalDateTime endTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    public enum SessionType {
        POMODORO("Pomodoro"),
        CUSTOM("Custom");

        private final String label;

        SessionType(String label) { this.label = label; }

        public String getLabel() { return label; }
    }

    // Constructors
    public FocusSession() {}

    public FocusSession(int durationMinutes, SessionType sessionType, User user) {
        this.durationMinutes = durationMinutes;
        this.sessionType = sessionType;
        this.user = user;
    }

    
    public void startSession() {
        if (this.startTime != null) {
            throw new IllegalStateException("Session already started");
        }
        this.startTime = LocalDateTime.now();
    }

    public void completeSession() {
        if (this.endTime != null) {
            throw new IllegalStateException("Session already ended");
        }
        this.successful = true;
        this.endTime = LocalDateTime.now();
    }

    // ✅ State Helpers
    public boolean isActive() {
        return startTime != null && endTime == null;
    }

    public long getElapsedMinutes() {
        if (startTime == null) return 0;
        return Duration.between(startTime, endTime != null ? endTime : LocalDateTime.now()).toMinutes();
    }

    public long getRemainingMinutes() {
        return Math.max(durationMinutes - getElapsedMinutes(), 0);
    }
    
 // Returns elapsed seconds for live countdown
    public long getElapsedSeconds() {
        if (startTime == null) return 0;
        return Duration.between(startTime, endTime != null ? endTime : LocalDateTime.now()).getSeconds();
    }

    // Reset session if user wants to restart
    public void resetSession() {
        this.startTime = null;
        this.endTime = null;
        this.successful = false;
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

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // Equals & HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FocusSession)) return false;
        FocusSession that = (FocusSession) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "FocusSession{" +
                "id=" + id +
                ", durationMinutes=" + durationMinutes +
                ", sessionType=" + sessionType +
                ", successful=" + successful +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
