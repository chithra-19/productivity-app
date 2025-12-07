package com.climbup.dto.response;

import java.time.LocalDate;
import java.util.List;

public class ProfileResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String bio;
    private String profilePictureUrl;

    private int streak;
    private int completedTasks;
    private int productivityScore;
    private LocalDate lastActiveDate;

    private boolean newAchievement;
    private List<String> achievementList;

    private Long userId;

    private List<BadgeResponseDTO> badges;   // 🔥 NEW

    // ---------------- Getters & Setters ----------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }

    public int getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }

    public int getProductivityScore() { return productivityScore; }
    public void setProductivityScore(int productivityScore) { this.productivityScore = productivityScore; }

    public LocalDate getLastActiveDate() { return lastActiveDate; }
    public void setLastActiveDate(LocalDate lastActiveDate) { this.lastActiveDate = lastActiveDate; }

    public boolean isNewAchievement() { return newAchievement; }
    public void setNewAchievement(boolean newAchievement) { this.newAchievement = newAchievement; }

    public List<String> getAchievementList() { return achievementList; }
    public void setAchievementList(List<String> achievementList) { this.achievementList = achievementList; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public List<BadgeResponseDTO> getBadges() { return badges; }
    public void setBadges(List<BadgeResponseDTO> badges) { this.badges = badges; }
}
