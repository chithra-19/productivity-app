package com.climbup.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(
    name = "streak_tracker",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "category"})
    }
)
public class StreakTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category; // Coding, Workout, Focus, etc.

    @Column(name = "current_streak", nullable = false)
    private int currentStreak = 0;

    @Column(name = "longest_streak", nullable = false)
    private int longestStreak = 0;

    @Column(name = "last_active_date")
    private LocalDate lastActiveDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /* ================= BUSINESS LOGIC ================= */

    public void updateForDay(LocalDate today, boolean qualifiedToday) {

        if (!qualifiedToday) {
            currentStreak = 0;
            return;
        }

        if (lastActiveDate == null) {
            currentStreak = 1;
        } else {
            long days = ChronoUnit.DAYS.between(lastActiveDate, today);

            if (days == 1) {
                currentStreak++;
            } else if (days > 1) {
                currentStreak = 1;
            }
            // days == 0 → already counted today
        }

        if (currentStreak > longestStreak) {
            longestStreak = currentStreak;
        }

        lastActiveDate = today;
    }

    /* ================= GETTERS / SETTERS ================= */

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
