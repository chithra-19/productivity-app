package com.climbup.dto.response;

import java.util.List;

public class HeatmapResponse {
    private final List<String> activeDates;
    private final int totalDays;
    private final int currentStreak;
    private final int maxStreak;
    private final int xp;
    private final int level;
    private final int nextLevelXP;
    private final double progress;
    private final List<HeatmapDTO> heatmapData;

    public HeatmapResponse(List<String> activeDates,
                           int totalDays,
                           int currentStreak,
                           int maxStreak,
                           int xp,
                           int level,
                           int nextLevelXP,
                           double progress,
                           List<HeatmapDTO> heatmapData) {
        this.activeDates = activeDates;
        this.totalDays = totalDays;
        this.currentStreak = currentStreak;
        this.maxStreak = maxStreak;
        this.xp = xp;
        this.level = level;
        this.nextLevelXP = nextLevelXP;
        this.progress = progress;
        this.heatmapData = heatmapData;
    }

    // Getters only (immutable DTO)
    public List<String> getActiveDates() { return activeDates; }
    public int getTotalDays() { return totalDays; }
    public int getCurrentStreak() { return currentStreak; }
    public int getMaxStreak() { return maxStreak; }
    public int getXp() { return xp; }
    public int getLevel() { return level; }
    public int getNextLevelXP() { return nextLevelXP; }
    public double getProgress() { return progress; }
    public List<HeatmapDTO> getHeatmapData() { return heatmapData; }
}