package com.climbup.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "daily_productivity_stats",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"})
)
public class ProductivityScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


	// The user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    // Focus & sessions
    @Column(nullable = false)
    private int focusedMinutes = 0;

    @Column(nullable = false)
    private int dailyGoalMinutes = 0;

    @Column(nullable = false)
    private int sessionCount = 0;

    // Tasks
    @Column(nullable = false)
    private int plannedTasks = 0;

    @Column(nullable = false)
    private int completedTasks = 0;

    // Focus quality
    private boolean quitEarly = false;
    private int tabSwitches = 0;
    private boolean idleDetected = false;

    // Calculated score
    @Column(nullable = false)
    private int productivityScore = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Getters and Setters...
    
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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public int getFocusedMinutes() {
		return focusedMinutes;
	}

	public void setFocusedMinutes(int focusedMinutes) {
		this.focusedMinutes = focusedMinutes;
	}

	public int getDailyGoalMinutes() {
		return dailyGoalMinutes;
	}

	public void setDailyGoalMinutes(int dailyGoalMinutes) {
		this.dailyGoalMinutes = dailyGoalMinutes;
	}

	public int getSessionCount() {
		return sessionCount;
	}

	public void setSessionCount(int sessionCount) {
		this.sessionCount = sessionCount;
	}

	public int getPlannedTasks() {
		return plannedTasks;
	}

	public void setPlannedTasks(int plannedTasks) {
		this.plannedTasks = plannedTasks;
	}

	public int getCompletedTasks() {
		return completedTasks;
	}

	public void setCompletedTasks(int completedTasks) {
		this.completedTasks = completedTasks;
	}

	public boolean isQuitEarly() {
		return quitEarly;
	}

	public void setQuitEarly(boolean quitEarly) {
		this.quitEarly = quitEarly;
	}

	public int getTabSwitches() {
		return tabSwitches;
	}

	public void setTabSwitches(int tabSwitches) {
		this.tabSwitches = tabSwitches;
	}

	public boolean isIdleDetected() {
		return idleDetected;
	}

	public void setIdleDetected(boolean idleDetected) {
		this.idleDetected = idleDetected;
	}

	public int getProductivityScore() {
		return productivityScore;
	}

	public void setProductivityScore(int productivityScore) {
		this.productivityScore = productivityScore;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

}
