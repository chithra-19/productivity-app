package com.climbup.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDate;

import java.util.*;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)   // ✅ ADD THIS
    @Column(name = "code", length = 50)  // ✅ ADD THIS
    private AchievementCode code;

	@NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(min = 8)
    @Column(nullable = false)
    private String password;
    
	@Column(name = "last_login_at")
    private Instant lastLoginAt;
	
    @Column(name = "current_streak", nullable = false)
    private int currentStreak = 0;

    @Column(name = "best_streak", nullable = false)
    private int bestStreak = 0;
    
    @Column(name = "productivityScore", nullable = false)
    private int productivityScore = 0;

	// Security Flags
    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean accountNonExpired = true;

    @Column(nullable = false)
    private boolean credentialsNonExpired = true;

    @Column(nullable = false)
    private boolean accountNonLocked = true;

    
    // Timestamps
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @Column(nullable = false)
    private int availableFreezes = 0;
    
    private LocalDate lastFreezeResetDate;

    // Tokens
    @Column(unique = true)
    private String verificationToken;
    
    @Column(unique = true)
    private String resetToken;
    private Instant resetTokenExpiry;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Profile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Goal> goals = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserAchievement> achievements = new HashSet<>();

 // XP & Level System
    @Column(nullable = false)
    private int xp = 0;

    @Column(nullable = false)
    private int level = 1;
  
    @Column(nullable = false)
    private int dailyGoalMinutes = 0;

    @Column(nullable = false)
    private int totalFocusMinutes = 0;
   
	public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getAvailableFreezes() {
		return availableFreezes;
	}

	public void setAvailableFreezes(int availableFreezes) {
		this.availableFreezes = availableFreezes;
	}

	public LocalDate getLastFreezeResetDate() {
		return lastFreezeResetDate;
	}

	public void setLastFreezeResetDate(LocalDate lastFreezeResetDate) {
		this.lastFreezeResetDate = lastFreezeResetDate;
	}

    
    public Instant getLastLoginAt() {
		return lastLoginAt;
	}

    
    public int getProductivityScore() {
  		return productivityScore;
  	}

  	public void setProductivityScore(int productivityScore) {
  		this.productivityScore = productivityScore;
  	}
	public void setLastLoginAt(Instant lastLoginAt) {
		this.lastLoginAt = lastLoginAt;
	}

	public List<FocusSession> getFocusSessions() {
		return focusSessions;
	}

	public void setFocusSessions(List<FocusSession> focusSessions) {
		this.focusSessions = focusSessions;
	}
	

    public void setId(Long id) {
		this.id = id;
	}

	 public void addFocusMinutes(int minutes) {
	        this.totalFocusMinutes += minutes;
	    }

	    public int getTotalFocusMinutes() {
	        return totalFocusMinutes;
	    }

	    public void setTotalFocusMinutes(int totalFocusMinutes) {
	        this.totalFocusMinutes = totalFocusMinutes;
	    }


	    public int getCurrentStreak() {
	        return currentStreak;
	    }

	    public void setCurrentStreak(int currentStreak) {
	        this.currentStreak = currentStreak;
	    }

	    public int getBestStreak() {
	        return bestStreak;
	    }

	    public void setBestStreak(int bestStreak) {
	        this.bestStreak = bestStreak;
	    }
    // Constructors
    public User() {}

    public User( String email, String password) {
      
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

    public void addAchievement(UserAchievement achievement) {
        achievements.add(achievement);
        achievement.setUser(this);
    }
    


    public int getDailyGoalMinutes() {
		return dailyGoalMinutes;
	}

	public void setDailyGoalMinutes(int dailyGoalMinutes) {
		this.dailyGoalMinutes = dailyGoalMinutes;
	}

    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FocusSession> focusSessions = new ArrayList<>();


    public void addFocusSession(FocusSession session) {
        focusSessions.add(session);
        session.setUser(this);
    }


    // Getters & Setters
    public Long getId() { return id; }
  
	
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

  
    public void setAccountNonExpired(boolean accountNonExpired) { this.accountNonExpired = accountNonExpired; }

  
    public void setCredentialsNonExpired(boolean credentialsNonExpired) { this.credentialsNonExpired = credentialsNonExpired; }

    
    public void setAccountNonLocked(boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; }

    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public Instant getResetTokenExpiry() { return resetTokenExpiry; }
    public void setResetTokenExpiry(Instant resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public Profile getProfile() { return profile; }

    public Set<Goal> getGoals() { return goals; }
    public Set<UserAchievement> getAchievements() { return achievements; }

    public void setTasks(Set<Task> tasks) { this.tasks = tasks; }
    public void setGoals(Set<Goal> goals) { this.goals = goals; }
   

 // ========= SPRING SECURITY REQUIRED METHODS ========= //
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return this.email;   // 🔥 THIS is the important line
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }


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
                ", email='" + email + '\'' +
                '}';
    }


}
