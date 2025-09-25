package com.climbup.dto.response;

public class UserProfileResponseDTO {

	private String username;
    private String email;
    private String avatarUrl;
    private int totalTasks;
    private int completedGoals;
    private int streakDays;

    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public int getTotalTasks() {
		return totalTasks;
	}

	public void setTotalTasks(int totalTasks) {
		this.totalTasks = totalTasks;
	}

	public int getCompletedGoals() {
		return completedGoals;
	}

	public void setCompletedGoals(int completedGoals) {
		this.completedGoals = completedGoals;
	}

	public int getStreakDays() {
		return streakDays;
	}

	public void setStreakDays(int streakDays) {
		this.streakDays = streakDays;
	}

    
    public UserProfileResponseDTO(String username, String email, String avatarUrl,
                          int totalTasks, int completedGoals, int streakDays) {
        this.username = username;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.totalTasks = totalTasks;
        this.completedGoals = completedGoals;
        this.streakDays = streakDays;
    }

    
}