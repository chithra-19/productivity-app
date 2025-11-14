package com.climbup.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "category", nullable = false)
    private String category; // e.g. "Coding", "Workout", "Focus"

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;
    
    private LocalDate date;


	@CreationTimestamp
    @Column(name = "logged_at", updatable = false)
    private LocalDateTime loggedAt;

    // ✅ Added these fields
    @Column(name = "task_count", nullable = false)
    private int taskCount = 0;

    @Column(name = "focus_minutes", nullable = false)
    private int focusMinutes = 0;

    // ----- Getters and Setters -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(LocalDate activityDate) {
        this.activityDate = activityDate;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public int getFocusMinutes() {
        return focusMinutes;
    }

    public void setFocusMinutes(int focusMinutes) {
        this.focusMinutes = focusMinutes;
    }
    
    public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

}
