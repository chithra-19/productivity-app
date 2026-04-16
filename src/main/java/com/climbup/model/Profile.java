package com.climbup.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= BASIC PROFILE INFO =================
    private String firstName;
    private String lastName;
    private String bio;
    private String profilePictureUrl;

    // ❌ REMOVED: email (belongs to User entity)

    // ================= STATS =================
    private int streak;
    private long completedTasks;
    private int productivityScore;
    private LocalDate lastActiveDate;

    @Column(name = "streak_freeze_count")
    private int streakFreezeCount = 1;

    // ================= ACHIEVEMENTS =================
    private boolean newAchievement;

    @ElementCollection
    @CollectionTable(
            name = "profile_achievements",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "achievement")
    private List<String> achievementList = new ArrayList<>();

    // ================= RELATIONSHIP =================
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // ================= CONSTRUCTORS =================
    public Profile() {}

    public Profile(String firstName, String lastName, String bio) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
    }

    // ================= GETTERS & SETTERS =================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }

    public long getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(long completedTasks) { this.completedTasks = completedTasks; }

    public int getProductivityScore() { return productivityScore; }
    public void setProductivityScore(int productivityScore) { this.productivityScore = productivityScore; }

    public LocalDate getLastActiveDate() { return lastActiveDate; }
    public void setLastActiveDate(LocalDate lastActiveDate) { this.lastActiveDate = lastActiveDate; }

    public int getStreakFreezeCount() { return streakFreezeCount; }
    public void setStreakFreezeCount(int streakFreezeCount) { this.streakFreezeCount = streakFreezeCount; }

    public boolean isNewAchievement() { return newAchievement; }
    public void setNewAchievement(boolean newAchievement) { this.newAchievement = newAchievement; }

    public List<String> getAchievementList() { return achievementList; }
    public void setAchievementList(List<String> achievementList) { this.achievementList = achievementList; }

    public User getUser() { return user; }

    public void setUser(User user) {
        this.user = user;

        if (user != null && user.getProfile() != this) {
            user.setProfile(this);
        }
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}