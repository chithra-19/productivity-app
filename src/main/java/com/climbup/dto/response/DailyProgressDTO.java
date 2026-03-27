package com.climbup.dto.response;

public class DailyProgressDTO {
    private int dailyFocusMinutes;
    private int dailyGoalMinutes;

    public DailyProgressDTO(int dailyFocusMinutes, int dailyGoalMinutes) {
        this.dailyFocusMinutes = dailyFocusMinutes;
        this.dailyGoalMinutes = dailyGoalMinutes;
    }

    public int getDailyFocusMinutes() { return dailyFocusMinutes; }
    public int getDailyGoalMinutes() { return dailyGoalMinutes; }
}