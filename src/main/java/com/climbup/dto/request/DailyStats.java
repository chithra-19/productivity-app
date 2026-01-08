package com.climbup.dto.request;


import java.time.LocalDate;

public class DailyStats {
    private LocalDate date;
    private int focusedMinutes;
    private int dailyGoalMinutes;
    private int sessionCount;
    private int plannedTasks;
    private int completedTasks;
    private boolean quitEarly;
    private int tabSwitches;
    private boolean idleDetected;

    // Getters & Setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getFocusedMinutes() { return focusedMinutes; }
    public void setFocusedMinutes(int focusedMinutes) { this.focusedMinutes = focusedMinutes; }

    public int getDailyGoalMinutes() { return dailyGoalMinutes; }
    public void setDailyGoalMinutes(int dailyGoalMinutes) { this.dailyGoalMinutes = dailyGoalMinutes; }

    public int getSessionCount() { return sessionCount; }
    public void setSessionCount(int sessionCount) { this.sessionCount = sessionCount; }

    public int getPlannedTasks() { return plannedTasks; }
    public void setPlannedTasks(int plannedTasks) { this.plannedTasks = plannedTasks; }

    public int getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }

    public boolean isQuitEarly() { return quitEarly; }
    public void setQuitEarly(boolean quitEarly) { this.quitEarly = quitEarly; }

    public int getTabSwitches() { return tabSwitches; }
    public void setTabSwitches(int tabSwitches) { this.tabSwitches = tabSwitches; }

    public boolean isIdleDetected() { return idleDetected; }
    public void setIdleDetected(boolean idleDetected) { this.idleDetected = idleDetected; }
}
