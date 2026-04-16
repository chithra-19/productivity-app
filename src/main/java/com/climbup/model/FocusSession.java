package com.climbup.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Duration;

import java.util.Objects;

import java.time.OffsetDateTime;

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

   
    @Column(name = "notes")
    private String notes;

	@Column(name = "start_time")
    private OffsetDateTime startTime;

    @Column(name = "end_time")
    private OffsetDateTime endTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionStatus status = SessionStatus.ACTIVE;
    
    @Column(name = "elapsed_minutes")
    private Integer elapsedMinutes;


    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType sessionType;
    
	public enum SessionType {
        FOCUS("Focus"),
        CUSTOM("Custom"),;

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
        if (startTime != null) {
            throw new IllegalStateException("Session already started");
        }
        this.startTime = OffsetDateTime.now(); // ✅ FIXED
        this.endTime = null;
        this.status = SessionStatus.ACTIVE;
    }
   

    // ✅ State Helpers
    public boolean isActive() {
    	return status == SessionStatus.ACTIVE;
    }

    // Reset session if user wants to restart
    public void resetSession() {
        this.startTime = null;
        this.endTime = null;
        this.status = SessionStatus.ACTIVE;
    }
    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public SessionType getSessionType() { return sessionType; }
    public void setSessionType(SessionType sessionType) { this.sessionType = sessionType; }

  
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    

    public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public SessionStatus getStatus() {
		return status;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setStatus(SessionStatus status) {
		this.status = status;
	}
	

    public OffsetDateTime getStartTime() {
		return startTime;
	}

	public OffsetDateTime getEndTime() {
		return endTime;
	}

	public void setStartTime(OffsetDateTime startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(OffsetDateTime endTime) {
		this.endTime = endTime;
	}

	
	public  Integer getElapsedMinutes() {
	    return elapsedMinutes;
	}

	public void setElapsedMinutes(int elapsedMinutes) {
	    this.elapsedMinutes = elapsedMinutes;
	}
	
	public void completeSession() {
	    if (startTime == null) throw new IllegalStateException("Session not started");
	    if (endTime != null) return;

	    this.endTime = OffsetDateTime.now();

	    long actual = Duration.between(startTime, endTime).toMinutes();
	    this.elapsedMinutes = (int) Math.min(actual, durationMinutes);

	    this.status = SessionStatus.COMPLETED;
	}

	public void abortSession() {
	    if (startTime == null) {
	        throw new IllegalStateException("Session not started");
	    }

	    this.endTime = OffsetDateTime.now();

	    long actual = Duration.between(startTime, endTime).toMinutes();
	    this.elapsedMinutes = (int) Math.max(0, Math.min(actual, durationMinutes));

	    // 🔥 smart status
	    this.status = (elapsedMinutes > 0)
	        ? SessionStatus.COMPLETED
	        : SessionStatus.ABORTED;
	}
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
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
