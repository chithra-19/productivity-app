package com.climbup.model;

import java.time.LocalDate;

public class HeatmapDay {
	
	private String date;
    private int taskCount;
    
    public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}

	

    public HeatmapDay(LocalDate date, int count) {
        this.date = date.toString();
        this.taskCount = count;
    }

    // getters and setters
}