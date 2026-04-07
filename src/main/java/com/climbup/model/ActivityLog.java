package com.climbup.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;


@Entity
@Table(
    name = "activity_log",
    indexes = {
        @Index(name = "idx_user_date", columnList = "user_id, activityDate"),
        @Index(name = "idx_activity_type", columnList = "type")
    }
)
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------- User Relation ----------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ---------- Activity Type (SAFE ENUM) ----------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;

    // ---------- Metrics ----------
    @Column(nullable = false)
    private Integer taskCount = 0;

    @Column(nullable = false)
    private Integer focusMinutes = 0;

    // Optional categorization (e.g., WORK, FITNESS, STUDY)
    @Column(length = 100)
    private String category;

    // Human-readable description
    @Column(nullable = false, length = 255)
    private String description;

    // Business date of activity
    @Column(nullable = false)
    private LocalDate activityDate;

    // Auto timestamp when record inserted
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant loggedAt;
    // ---------- Constructors ----------

    public ActivityLog() {
    }

    public ActivityLog(User user,
                       ActivityType type,
                       String description,
                       LocalDate activityDate) {
        this.user = user;
        this.type = type;
        this.description = description;
        this.activityDate = activityDate;
    }

    // ---------- Getters & Setters ----------

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }

    public Integer getFocusMinutes() {
        return focusMinutes;
    }

    public void setFocusMinutes(Integer focusMinutes) {
        this.focusMinutes = focusMinutes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(LocalDate activityDate) {
        this.activityDate = activityDate;
    }

    public Instant getLoggedAt() {
        return loggedAt;
    }

    
    
}
