package com.climbup.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "achievements")
@NamedQueries({
    @NamedQuery(
        name = "Achievement.findByTitle",
        query = "SELECT a FROM Achievement a WHERE a.title = :title"
    ),
    @NamedQuery(
        name = "Achievement.findByUserAndType",
        query = "SELECT a FROM Achievement a WHERE a.user = :user AND a.type = :type"
    )
})
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "icon", length = 255)
    private String icon; // URL or emoji

    @NotBlank
    @Size(max = 100)
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private Type type = Type.GENERAL;

    @Size(max = 100)
    @Column(name = "category", length = 100)
    private String category; // Optional, e.g., "Coding", "Focus"

    @Column(name = "unlocked", nullable = false)
    private boolean unlocked = false;

    @Column(name = "newly_unlocked", nullable = false)
    private boolean newlyUnlocked = false;

    @Column(name = "seen", nullable = false)
    private boolean seen = false;

    @Column(name = "unlocked_date")
    private LocalDate unlockedDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "code", unique = true, length = 100)
    private AchievementCode code;


    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true)
    private User user;

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt;

    // ------------------ Enum ------------------
    public enum Type {
        STREAK, GOAL, TASK, GENERAL
    }

    // ------------------ Constructors ------------------
    public Achievement() {}

    public Achievement(String title, String description, Type type, User user) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.user = user;
    }

    public Achievement(User user, Type type, String title, String description) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.description = description;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    public Goal getGoal() { return goal; }
    public void setGoal(Goal goal) { this.goal = goal; }

    
    // ------------------ Getters & Setters ------------------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }

    public boolean isNewlyUnlocked() { return newlyUnlocked; }
    public void setNewlyUnlocked(boolean newlyUnlocked) { this.newlyUnlocked = newlyUnlocked; }

    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }

    public LocalDate getUnlockedDate() { return unlockedDate; }
    public void setUnlockedDate(LocalDate unlockedDate) { this.unlockedDate = unlockedDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(LocalDateTime unlockedAt) { this.unlockedAt = unlockedAt; }

    public AchievementCode getCode() { return code; }
    public void setCode(AchievementCode code) { this.code = code; }


    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // ------------------ Utility Methods ------------------
    /** Unlock this achievement safely */
    public void unlock() {
        if (!this.unlocked) {
            this.unlocked = true;
            this.newlyUnlocked = true;
            this.unlockedDate = LocalDate.now();
            this.unlockedAt = LocalDateTime.now();
        }
    }

    // ------------------ Lifecycle Hooks ------------------
    @PrePersist
    @PreUpdate
    protected void onSave() {
        if (this.unlocked && this.unlockedDate == null) {
            this.unlockedDate = LocalDate.now();
        }
        if (this.unlocked && this.unlockedAt == null) {
            this.unlockedAt = LocalDateTime.now();
        }
    }

    // ------------------ equals & hashCode ------------------
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

    @Override
    public String toString() {
        return "Achievement{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", unlocked=" + unlocked +
                ", newlyUnlocked=" + newlyUnlocked +
                ", seen=" + seen +
                '}';
    }
    
    public enum AchievementCode {
        GOAL_1,
        GOAL_5,
        GOAL_10,
        BEFORE_DEADLINE,
        STREAK_3,
        STREAK_7,
        HEATMAP_50,
        FIRST_STEP,
        STREAK_STARTER,
        TASK_MASTER,
        EARLY_BIRD,
        PRODUCTIVITY_PRO,
        GOAL_COMPLETED
    }
}
