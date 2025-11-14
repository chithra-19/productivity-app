package com.climbup.model;

public class ActivityHeatmap {
	    private int totalDays;
	    private int maxStreak;

	    // Constructors, getters, setters
	    public ActivityHeatmap(int totalDays, int maxStreak) {
	        this.totalDays = totalDays;
	        this.maxStreak = maxStreak;
	    }

	    public int getTotalDays() {
	        return totalDays;
	    }

	    public int getMaxStreak() {
	        return maxStreak;
	    }

	    @Override
	    public String toString() {
	        return "Activity Heatmap\nTotal Days - " + totalDays + "\nMax Streak - " + maxStreak;
	    }
}

