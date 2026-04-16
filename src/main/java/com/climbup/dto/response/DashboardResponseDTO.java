package com.climbup.dto.response;

import java.util.Map;

public class DashboardResponseDTO {

    // ===== User =====
    private String firstName;

    // ===== XP + Level =====
    private int level;
    private int xp;
    private int xpForNextLevel;
    private int xpProgress;

    // ===== Streak =====
    private int currentStreak;
    private int bestStreak;

    // ===== Productivity =====
    private int productivityScore;
    private String productivityLabel;

    // ===== Task Stats =====
    private Map<String, Integer> taskStats;

    // ===== Getters & Setters =====

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getXpForNextLevel() {
        return xpForNextLevel;
    }

    public void setXpForNextLevel(int xpForNextLevel) {
        this.xpForNextLevel = xpForNextLevel;
    }

    public int getXpProgress() {
        return xpProgress;
    }

    public void setXpProgress(int xpProgress) {
        this.xpProgress = xpProgress;
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

    public Map<String, Integer> getTaskStats() {
        return taskStats;
    }

    public void setTaskStats(Map<String, Integer> taskStats) {
        this.taskStats = taskStats;
    }
    
    
}