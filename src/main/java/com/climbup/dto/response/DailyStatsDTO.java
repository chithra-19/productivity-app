package com.climbup.dto.response;

public class DailyStatsDTO {
    private int dailyFocusMinutes;
    private int dailyGoalMinutes;
    private long sessionsCompletedToday;

    public DailyStatsDTO(int dailyFocusMinutes, int dailyGoalMinutes, long sessionsCompletedToday) {
        this.dailyFocusMinutes = dailyFocusMinutes;
        this.dailyGoalMinutes = dailyGoalMinutes;
        this.sessionsCompletedToday = sessionsCompletedToday;
    }

    public int getDailyFocusMinutes() { return dailyFocusMinutes; }
    public int getDailyGoalMinutes() { return dailyGoalMinutes; }
    public long getSessionsCompletedToday() { return sessionsCompletedToday; }
}