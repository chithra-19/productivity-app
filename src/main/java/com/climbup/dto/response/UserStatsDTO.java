package com.climbup.dto.response;

public class UserStatsDTO {
    private int currentStreak;
    private int bestStreak;
    private int productivityScore;

    // ✅ Constructor
    public UserStatsDTO(int currentStreak, int bestStreak, int productivityScore) {
        this.currentStreak = currentStreak;
        this.bestStreak = bestStreak;
        this.productivityScore = productivityScore;
    }

    // ✅ Getters & Setters
    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }

    public int getProductivityScore() {
        return productivityScore;
    }

    public void setProductivityScore(int productivityScore) {
        this.productivityScore = productivityScore;
    }
}
