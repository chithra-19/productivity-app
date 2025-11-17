package com.climbup.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

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

    // 🔧 Utility method to check if today continues the streak
    public boolean isStreakContinuing(LocalDate today) {
        return lastActiveDate != null && lastActiveDate.plusDays(1).equals(today);
    }

    // 🔁 Update streak based on today's activity
    public void updateStreak(LocalDate today) {
        if (lastActiveDate == null || lastActiveDate.plusDays(1).equals(today)) {
            currentStreak++;
        } else if (!lastActiveDate.equals(today)) {
            currentStreak = 1;
        }
        longestStreak = Math.max(longestStreak, currentStreak);
        lastActiveDate = today;
    }

    // 🧩 Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void applyStreakLogic(LocalDate today, Consumer<String> logger) {
        if (lastActiveDate == null) {
            currentStreak = 1;
            longestStreak = 1;
            logger.accept("Started first streak 🔥");
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastActiveDate, today);

            if (daysBetween == 1) {
                currentStreak++;
                if (currentStreak > longestStreak) {
                    longestStreak = currentStreak;
                    logger.accept("New longest streak: " + currentStreak + " days 🏆");
                } else {
                    logger.accept("Streak continued: " + currentStreak + " days ✅");
                }
            } else if (daysBetween > 1) {
                currentStreak = 1;
                logger.accept("Streak reset after " + daysBetween + " days 😢");
            } else {
                logger.accept("Streak already updated today 📅");
            }
        }
        lastActiveDate = today;
    }
    
    public boolean hasUpdatedToday(LocalDate today) {
        return lastActiveDate != null && lastActiveDate.equals(today);
    }

	public LocalDate getDate() {
		// TODO Auto-generated method stub
		return null;
	}
}