package com.climbup.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(min = 8)
    @Column(nullable = false)
    private String password;

    // Productivity & Streak
    @Column(name = "productivity_score", nullable = false)
    private Integer productivityScore = 0;

    @Column(name = "current_streak", nullable = false)
    private Integer currentStreak = 0;

    @Column(name = "best_streak", nullable = false)
    private Integer bestStreak = 0;

    @Column(name = "last_active_date")
    private LocalDate lastActiveDate;

    // Security flags
    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonLocked = true;

    // Timestamps
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Tokens
    private String verificationToken;
    private String resetToken;
    private LocalDateTime resetTokenExpiry;

    // Relationships
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private Profile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Goal> goals = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Achievement> achievements = new HashSet<>();

    // Constructors
    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Relationship helpers
    public void setProfile(Profile profile) {
        this.profile = profile;
        if (profile != null) profile.setUser(this);
    }

    public void addTask(Task task) {
        tasks.add(task);
        task.setUser(this);
    }

    public void addGoal(Goal goal) {
        goals.add(goal);
        goal.setUser(this);
    }

    public void addAchievement(Achievement achievement) {
        achievements.add(achievement);
        achievement.setUser(this);
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getProductivityScore() { return productivityScore == null ? 0 : productivityScore; }
    public void setProductivityScore(Integer productivityScore) { this.productivityScore = productivityScore; }

    public Integer getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(Integer currentStreak) { this.currentStreak = currentStreak; }

    public Integer getBestStreak() { return bestStreak; }
    public void setBestStreak(Integer bestStreak) { this.bestStreak = bestStreak; }

    public LocalDate getLastActiveDate() { return lastActiveDate; }
    public void setLastActiveDate(LocalDate lastActiveDate) { this.lastActiveDate = lastActiveDate; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isAccountNonExpired() { return accountNonExpired; }
    public void setAccountNonExpired(boolean accountNonExpired) { this.accountNonExpired = accountNonExpired; }

    public boolean isCredentialsNonExpired() { return credentialsNonExpired; }
    public void setCredentialsNonExpired(boolean credentialsNonExpired) { this.credentialsNonExpired = credentialsNonExpired; }

    public boolean isAccountNonLocked() { return accountNonLocked; }
    public void setAccountNonLocked(boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; }

    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public LocalDateTime getResetTokenExpiry() { return resetTokenExpiry; }
    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Profile getProfile() { return profile; }

    public Set<Task> getTasks() { return tasks; }
    public Set<Goal> getGoals() { return goals; }
    public Set<Achievement> getAchievements() { return achievements; }

    public void setTasks(Set<Task> tasks) { this.tasks = tasks; }
    public void setGoals(Set<Goal> goals) { this.goals = goals; }
    public void setAchievements(Set<Achievement> achievements) { this.achievements = achievements; }

    // equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    // toString
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", productivityScore=" + productivityScore +
                '}';
    }
}
