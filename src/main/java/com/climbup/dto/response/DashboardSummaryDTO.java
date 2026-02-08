package com.climbup.dto.response;

import java.util.Map;

public class DashboardSummaryDTO {

    private int level;
    private int xp;
    private int xpProgress;
    private int xpForNextLevel;

    private int currentStreak;
    private int bestStreak;

    private Map<String, Integer> taskStats;

    // getters & setters
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

    public int getXpProgress() {
        return xpProgress;
    }

    public void setXpProgress(int xpProgress) {
        this.xpProgress = xpProgress;
    }

    public int getXpForNextLevel() {
        return xpForNextLevel;
    }

    public void setXpForNextLevel(int xpForNextLevel) {
        this.xpForNextLevel = xpForNextLevel;
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
