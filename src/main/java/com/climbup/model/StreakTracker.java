package com.climbup.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "streak_tracker")
public class StreakTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", nullable = false)
    private String category; // e.g. "Coding", "Workout", "Focus"

    @Column(name = "current_streak", nullable = false)
    private int currentStreak = 0;

    @Column(name = "longest_streak", nullable = false)
    private int longestStreak = 0;

    @Column(name = "last_active_date")
    private LocalDate lastActiveDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 🔧 Check if streak was updated today
    public boolean hasUpdatedToday(LocalDate today) {
        return lastActiveDate != null && lastActiveDate.equals(today);
    }

    // 🔁 Update streak for a day (used by TaskService/StreakTrackerService)
    public void updateForDay(LocalDate today, boolean qualifiedToday) {
        if (!qualifiedToday) {
            currentStreak = 0; // fail to meet criteria resets streak
        } else if (lastActiveDate == null) {
            currentStreak = 1; // first streak
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastActiveDate, today);
            if (daysBetween == 1) {
                currentStreak++; // streak continues
            } else if (daysBetween > 1) {
                currentStreak = 1; // streak broken
            } // if daysBetween == 0, already updated today, do nothing
        }
        // always update longest streak if needed
        if (currentStreak > longestStreak) {
            longestStreak = currentStreak;
        }
        // mark last active date
        if (qualifiedToday) lastActiveDate = today;
    }

    // 🧩 Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }
    public LocalDate getLastActiveDate() { return lastActiveDate; }
    public void setLastActiveDate(LocalDate lastActiveDate) { this.lastActiveDate = lastActiveDate; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
