package com.climbup.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private int progress = 0; // progress in percentage (0–100)

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status = GoalStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(nullable = false)
    private boolean dropped = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ==== ENUMS ====
    public enum GoalStatus { ACTIVE, COMPLETED, DROPPED }
    public enum Priority { LOW, MEDIUM, HIGH }

    // ==== CONSTRUCTORS ====
    public Goal() {}

    public Goal(String title, String description, LocalDate dueDate, User user) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.user = user;
        this.status = GoalStatus.ACTIVE;
        this.priority = Priority.MEDIUM;
    }

    // ==== BUSINESS LOGIC ====
    public void markCompleted() {
        this.completed = true;
        this.status = GoalStatus.COMPLETED;
        this.progress = 100;
    }

    public void dropGoal() {
        this.dropped = true;
        this.status = GoalStatus.DROPPED;
        this.completed = false;
    }

    // ==== GETTERS & SETTERS ====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public GoalStatus getStatus() { return status; }
    public void setStatus(GoalStatus status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public boolean isDropped() { return dropped; }
    public void setDropped(boolean dropped) { this.dropped = dropped; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // ==== toString ====
    @Override
    public String toString() {
        return "Goal{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", progress=" + progress +
                ", completed=" + completed +
                ", dropped=" + dropped +
                ", dueDate=" + dueDate +
                '}';
    }
}
