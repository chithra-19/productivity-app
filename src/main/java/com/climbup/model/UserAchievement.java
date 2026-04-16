package com.climbup.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;


import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;

@Entity
@Table(
    name = "user_achievements",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "template_id"})
    }
)
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "template_id")
    private AchievementTemplate template;

    @Column(nullable = false)
    private boolean unlocked = false;

    @Column(nullable = false)
    private boolean newlyUnlocked = false;

    @Column(nullable = false)
    private boolean seen = false;

    private Instant unlockedAt;

    @ManyToOne
    @JoinColumn(name = "goal_id", nullable = true)
    @JsonIgnore
    private Goal goal;
    
    
	@CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // -------- logic --------

	public void unlock() {
	    if (this.unlocked) return;

	    this.unlocked = true;
	    this.newlyUnlocked = true;
	    this.unlockedAt = Instant.now();
	}

	public void markSeen() {
	    this.seen = true;
	    this.newlyUnlocked = false;
	}

    // -------- getters/setters --------

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public AchievementTemplate getTemplate() { return template; }
    public void setTemplate(AchievementTemplate template) { this.template = template; }

    public boolean isUnlocked() { return unlocked; }
    public boolean isNewlyUnlocked() { return newlyUnlocked; }
    public boolean isSeen() { return seen; }

    public Instant getUnlockedAt() { return unlockedAt; }
    
    public Instant getCreatedAt() {
		return createdAt;
	}


	public void setUnlocked(boolean unlocked) {
		this.unlocked = unlocked;
	}

	public void setNewlyUnlocked(boolean newlyUnlocked) {
		this.newlyUnlocked = newlyUnlocked;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	public void setUnlockedAt(Instant unlockedAt) {
		this.unlockedAt = unlockedAt;
	}

	public Goal getGoal() {
	    return goal;
	}

	public void setGoal(Goal goal) {
	    this.goal = goal;
	}
	

}