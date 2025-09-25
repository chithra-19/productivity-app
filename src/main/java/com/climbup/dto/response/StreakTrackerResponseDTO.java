package com.climbup.dto.response;

import java.time.LocalDate;

public class StreakTrackerResponseDTO {

    private Long id;
    private String category;         // e.g., "Coding", "Workout"
    private int currentStreak;       // current consecutive days
    private int longestStreak;       // longest consecutive days
    private LocalDate lastActiveDate; // last day streak was updated

    // Optional: timestamps if needed
    // private LocalDateTime createdAt;
    // private LocalDateTime updatedAt;

    // 🔧 Getters and Setters
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
}
