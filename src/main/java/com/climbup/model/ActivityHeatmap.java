package com.climbup.model;

public class ActivityHeatmap {

    private final int totalDays;
    private final int maxStreak;

    public ActivityHeatmap(int totalDays, int maxStreak) {
        this.totalDays = totalDays;
        this.maxStreak = maxStreak;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public int getMaxStreak() {
        return maxStreak;
    }

    @Override
    public String toString() {
        return String.format("Activity Heatmap%nTotal Days: %d%nMax Streak: %d", totalDays, maxStreak);
    }
}