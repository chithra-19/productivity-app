package com.climbup.dto.response;

public class HeatmapDTO {
    private String date;           // formatted as "yyyy-MM-dd"
    private int taskCount;
    private int focusMinutes;
    private boolean isStreakDay;

    public HeatmapDTO(String date, int taskCount, int focusMinutes, boolean isStreakDay) {
        this.date = date;
        this.taskCount = taskCount;
        this.focusMinutes = focusMinutes;
        this.isStreakDay = isStreakDay;
    }

    // Getters
    public String getDate() {
        return date;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public int getFocusMinutes() {
        return focusMinutes;
    }

    public boolean isStreakDay() {
        return isStreakDay;
    }

    // Setters
    public void setDate(String date) {
        this.date = date;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public void setFocusMinutes(int focusMinutes) {
        this.focusMinutes = focusMinutes;
    }

    public void setStreakDay(boolean streakDay) {
        isStreakDay = streakDay;
    }
}