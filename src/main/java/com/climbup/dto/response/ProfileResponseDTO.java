package com.climbup.dto.response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProfileResponseDTO {

    // ================= PROFILE INFO =================
    private Long userId;
    private String firstName;
    private String lastName;
    private String bio;
    private String profilePictureUrl;
    private String email;

    
	// ================= ACTIVITY =================
    private LocalDate lastActiveDate;

    // ================= STATS =================
    private int productivityScore;
    private long completedTasks;

    // ================= STREAK =================
    private int currentStreak;
    private int bestStreak;

    // ================= XP SYSTEM =================
    private long xp;
    private int level;
    private int levelProgress;

    // ================= ACHIEVEMENTS =================
    private boolean newAchievement;

    private List<String> achievementList = new ArrayList<>();

    // ---------- Getters & Setters ----------

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public LocalDate getLastActiveDate() { return lastActiveDate; }
    public void setLastActiveDate(LocalDate lastActiveDate) { this.lastActiveDate = lastActiveDate; }

    public int getProductivityScore() { return productivityScore; }
    public void setProductivityScore(int productivityScore) { this.productivityScore = productivityScore; }

    public long getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(long completedTasks) { this.completedTasks = completedTasks; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getBestStreak() { return bestStreak; }
    public void setBestStreak(int bestStreak) { this.bestStreak = bestStreak; }

    public long getXp() { return xp; }
    public void setXp(long xp) { this.xp = xp; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getLevelProgress() { return levelProgress; }
    public void setLevelProgress(int levelProgress) { this.levelProgress = levelProgress; }

    public boolean isNewAchievement() { return newAchievement; }
    public void setNewAchievement(boolean newAchievement) { this.newAchievement = newAchievement; }

    public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
    public List<String> getAchievementList() { return achievementList; }
    public void setAchievementList(List<String> achievementList) {
        this.achievementList = achievementList != null ? achievementList : new ArrayList<>();
    }
	
}