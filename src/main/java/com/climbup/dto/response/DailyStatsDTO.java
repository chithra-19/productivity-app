package com.climbup.dto.response;

public class DailyStatsDTO {

    private int dailyFocusMinutes;
    private int dailyGoalMinutes;
    private long sessionsCompletedToday;
    private int totalFocusMinutes;

    public DailyStatsDTO() {
    }

    public int getDailyFocusMinutes() {
        return dailyFocusMinutes;
    }

    public void setDailyFocusMinutes(int dailyFocusMinutes) {
        this.dailyFocusMinutes = dailyFocusMinutes;
    }

    public int getDailyGoalMinutes() {
        return dailyGoalMinutes;
    }

    public void setDailyGoalMinutes(int dailyGoalMinutes) {
        this.dailyGoalMinutes = dailyGoalMinutes;
    }

    public long getSessionsCompletedToday() {
        return sessionsCompletedToday;
    }

    public void setSessionsCompletedToday(long sessionsCompletedToday) {
        this.sessionsCompletedToday = sessionsCompletedToday;
    }

    public int getTotalFocusMinutes() {
        return totalFocusMinutes;
    }

    public void setTotalFocusMinutes(int totalFocusMinutes) {
        this.totalFocusMinutes = totalFocusMinutes;
    }
}