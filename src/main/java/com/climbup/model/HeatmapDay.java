package com.climbup.model;

import java.time.LocalDate;

public class HeatmapDay {

    private final String date;
    private final int taskCount;

    public HeatmapDay(LocalDate date, int taskCount) {
        this.date = date.toString(); // ISO format: yyyy-MM-dd
        this.taskCount = taskCount;
    }

    public String getDate() {
        return date;
    }

    public int getTaskCount() {
        return taskCount;
    }
}