package com.climbup.dto.response;

import java.util.List;

public class HeatmapResponse {
    private List<String> activeDates;     // formatted for UI
    private int totalDays;
    private int currentStreak;
    private List<HeatmapDTO> heatmapData; // optional: per-day stats

    public HeatmapResponse() {
    }

    public HeatmapResponse(List<String> activeDates, int totalDays,
                           int currentStreak, List<HeatmapDTO> heatmapData) {
        this.activeDates = activeDates;
        this.totalDays = totalDays;
        this.currentStreak = currentStreak;
        this.heatmapData = heatmapData;
    }

    // Getters
    public List<String> getActiveDates() {
        return activeDates;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public List<HeatmapDTO> getHeatmapData() {
        return heatmapData;
    }

    // Setters
    public void setActiveDates(List<String> activeDates) {
        this.activeDates = activeDates;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public void setHeatmapData(List<HeatmapDTO> heatmapData) {
        this.heatmapData = heatmapData;
    }
}