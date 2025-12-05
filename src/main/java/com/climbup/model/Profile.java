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

    // ---------- Basic Info ----------
    private String firstName;
    private String lastName;
    private String bio;
    private String profilePictureUrl;
    private String email;

    // ---------- Stats ----------
    private int streak;
    private int completedTasks;
    private int productivityScore;
    private LocalDate lastActiveDate;

    // ---------- Achievements ----------
    private boolean newAchievement;

    @ElementCollection
    @CollectionTable(name = "profile_achievements", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "achievement")
    private List<String> achievementList = new ArrayList<>();

    // ---------- Relationship ----------
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ---------- Constructors ----------
    public Profile() {}

    public Profile(String firstName, String lastName, String bio, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.email = email;
    }

    // ---------- Getters & Setters ----------
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

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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

    public User getUser() { return user; }

    public void setUser(User user) {
        this.user = user;
        if (user != null && user.getProfile() != this) {
            user.setProfile(this); // maintain bidirectional link
        }
    }

    // ---------- toString ----------
    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
