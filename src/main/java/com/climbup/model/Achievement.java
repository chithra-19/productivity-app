package com.climbup.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
    name = "achievements",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "code"})
    }
)
public class Achievement {

    public void setId(Long id) {
		this.id = id;
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

	public void setUnlockedDate(LocalDateTime unlockedDate) {
		this.unlockedDate = unlockedDate;
	}

	public void setUnlockedAt(LocalDateTime unlockedAt) {
		this.unlockedAt = unlockedAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	 @ManyToOne
	    @JoinColumn(name = "goal_id")
	    private Goal goal;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ------------------ Badge Identity ------------------

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, length = 100)
    private AchievementCode code;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String title;

    @Size(max = 255)
    private String description;

    @Column(length = 255)
    private String icon; // emoji or icon class

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Type type;

    @Column(length = 100)
    private String category;

    // ------------------ Unlock State ------------------

    @Column(nullable = false)
    private boolean unlocked = false;

    @Column(name = "newly_unlocked", nullable = false)
    private boolean newlyUnlocked = false;

    @Column(nullable = false)
    private boolean seen = false;

    @Column(name = "unlocked_date")
    private LocalDateTime unlockedDate;

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt;

    // ------------------ Relations ------------------

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ------------------ Timestamps ------------------

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    // ------------------ Enums ------------------

    public enum Type {
        GOAL,
        TASK,
        STREAK,
        GENERAL
    }

    public enum AchievementCode {
        FIRST_STEP,        // first goal created
        GOAL_1,            // 1 goal completed
        GOAL_5,            // 5 goals completed
        GOAL_10,           // 10 goals completed
        STREAK_3,
        STREAK_7,
        TASK_MASTER,
        PRODUCTIVITY_PRO,
        EARLY_BIRD,
        GOAL_COMPLETED,
    }

    // ------------------ Constructors ------------------

    public Achievement() {}

    public Achievement(User user, AchievementCode code, String title,
                       String description, Type type, String icon) {
        this.user = user;
        this.code = code;
        this.title = title;
        this.description = description;
        this.type = type;
        this.icon = icon;
        this.unlocked = false;
        this.newlyUnlocked = false;
        this.seen = false;
    }

    // ------------------ Business Logic ------------------

    public void unlock() {
        if (!this.unlocked) {
            this.unlocked = true;
            this.newlyUnlocked = true;
            this.unlockedDate = LocalDateTime.now();
            this.unlockedAt = LocalDateTime.now();
        }
    }

    public void markSeen() {
        this.seen = true;
        this.newlyUnlocked = false;
    }

    // ------------------ Getters & Setters ------------------

    public Long getId() { return id; }

    public AchievementCode getCode() { return code; }
    public void setCode(AchievementCode code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isUnlocked() { return unlocked; }
    public boolean isNewlyUnlocked() { return newlyUnlocked; }
    public boolean isSeen() { return seen; }

    public LocalDateTime getUnlockedDate() { return unlockedDate; }
    public LocalDateTime getUnlockedAt() { return unlockedAt; }

    
    public Goal getGoal() { return goal; }
    public void setGoal(Goal goal) { this.goal = goal; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    // ------------------ Equality ------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Achievement)) return false;
        Achievement that = (Achievement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
