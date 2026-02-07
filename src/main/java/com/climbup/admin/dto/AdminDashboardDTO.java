package com.climbup.admin.dto;

import java.util.List;

public class AdminDashboardDTO {


	private long totalUsers;
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long activeUsersLast7Days;

    private List<RecentActivityDTO> recentActivities;

    // getters & setters
    
    public long getTotalUsers() {
		return totalUsers;
	}

	public void setTotalUsers(long totalUsers) {
		this.totalUsers = totalUsers;
	}

	public long getTotalTasks() {
		return totalTasks;
	}

	public void setTotalTasks(long totalTasks) {
		this.totalTasks = totalTasks;
	}

	public long getCompletedTasks() {
		return completedTasks;
	}

	public void setCompletedTasks(long completedTasks) {
		this.completedTasks = completedTasks;
	}

	public long getPendingTasks() {
		return pendingTasks;
	}

	public void setPendingTasks(long pendingTasks) {
		this.pendingTasks = pendingTasks;
	}

	public long getActiveUsersLast7Days() {
		return activeUsersLast7Days;
	}

	public void setActiveUsersLast7Days(long activeUsersLast7Days) {
		this.activeUsersLast7Days = activeUsersLast7Days;
	}

	public List<RecentActivityDTO> getRecentActivities() {
		return recentActivities;
	}

	public void setRecentActivities(List<RecentActivityDTO> recentActivities) {
		this.recentActivities = recentActivities;
	}

}
