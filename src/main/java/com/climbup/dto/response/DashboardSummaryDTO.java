package com.climbup.dto.response;

import java.util.Map;

public class DashboardSummaryDTO {

    // 🔥 Core productivity metric
    private int productivityScore;      // 0–100
    private String productivityLabel;   // LOW / MEDIUM / HIGH

    // 🔥 Streaks
    private int currentStreak;
    private int bestStreak;
    
    private String firstName;

    // 🔥 Task stats
    private Map<String, Integer> taskStats;

    // ===== getters & setters =====

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public int getProductivityScore() {
        return productivityScore;
    }

    public void setProductivityScore(int productivityScore) {
        this.productivityScore = productivityScore;
    }

    public String getProductivityLabel() {
        return productivityLabel;
    }

    public void setProductivityLabel(String productivityLabel) {
        this.productivityLabel = productivityLabel;
    }

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

    public Map<String, Integer> getTaskStats() {
        return taskStats;
    }

    public void setTaskStats(Map<String, Integer> taskStats) {
        this.taskStats = taskStats;
    }
}
