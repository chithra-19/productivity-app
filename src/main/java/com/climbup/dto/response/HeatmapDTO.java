package com.climbup.dto.response;

public class HeatmapDTO {
    private final String date;       // formatted as "yyyy-MM-dd"
    private final int taskCount;
    private final int focusMinutes;
    private final boolean isStreakDay;

    public HeatmapDTO(String date, int taskCount, int focusMinutes, boolean isStreakDay) {
        this.date = date;
        this.taskCount = taskCount;
        this.focusMinutes = focusMinutes;
        this.isStreakDay = isStreakDay;
    }

    public String getDate() { return date; }
    public int getTaskCount() { return taskCount; }
    public int getFocusMinutes() { return focusMinutes; }
    public boolean isStreakDay() { return isStreakDay; }
}