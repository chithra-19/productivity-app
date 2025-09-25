package com.climbup.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "streak_challenges")
public class StreakChallenge {

    public enum ChallengeType {
        TWENTY_ONE_DAYS(21),
        FIFTY_DAYS(50),
        HUNDRED_DAYS(100);

        private final int days;
        ChallengeType(int days) { this.days = days; }
        public int getDays() { return days; }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "challenge_type", nullable = false)
    private ChallengeType challengeType;

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @Column(name = "current_streak", nullable = false)
    private int currentStreak = 0;

    @Column(name = "completed", nullable = false)
    private boolean completed = false;

    @Column(name = "achievement_unlocked", nullable = false)
    private boolean achievementUnlocked = false;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "last_completed_date")
    private LocalDate lastCompletedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 🛠️ Constructors
    public StreakChallenge() {}

    public StreakChallenge(ChallengeType challengeType, User user) {
        this.challengeType = challengeType;
        this.durationDays = challengeType.getDays();
        this.user = user;
        this.startDate = LocalDate.now();
    }

    // 🔧 Utility Methods
    public void markDayCompleted(LocalDate today) {
        if (lastCompletedDate != null) {
            // If missed a day, reset
            if (lastCompletedDate.plusDays(1).isBefore(today)) {
                resetStreak();
            }
            // Prevent multiple completions in same day
            if (lastCompletedDate.equals(today)) {
                return;
            }
        }

        currentStreak++;
        lastCompletedDate = today;

        if (currentStreak >= durationDays) {
            completed = true;
            achievementUnlocked = true;
        }
    }

    public void resetStreak() {
        currentStreak = 0;
        completed = false;
        achievementUnlocked = false;
        lastCompletedDate = null;
    }

    // ✅ Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ChallengeType getChallengeType() { return challengeType; }
    public void setChallengeType(ChallengeType challengeType) {
        this.challengeType = challengeType;
        this.durationDays = challengeType.getDays();
    }

    public int getDurationDays() { return durationDays; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public boolean isAchievementUnlocked() { return achievementUnlocked; }
    public void setAchievementUnlocked(boolean achievementUnlocked) { this.achievementUnlocked = achievementUnlocked; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getLastCompletedDate() { return lastCompletedDate; }
    public void setLastCompletedDate(LocalDate lastCompletedDate) { this.lastCompletedDate = lastCompletedDate; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // 🔁 equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreakChallenge)) return false;
        StreakChallenge that = (StreakChallenge) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user);
    }

    // 🧾 toString
    @Override
    public String toString() {
        return "StreakChallenge{" +
                "id=" + id +
                ", challengeType=" + challengeType +
                ", durationDays=" + durationDays +
                ", currentStreak=" + currentStreak +
                ", completed=" + completed +
                ", achievementUnlocked=" + achievementUnlocked +
                ", startDate=" + startDate +
                ", lastCompletedDate=" + lastCompletedDate +
                '}';
    }
}
