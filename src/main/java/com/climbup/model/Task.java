package com.climbup.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "tasks")

public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @Size(max = 500)
    private String description;
    
    @Column(name = "focus_hours")
    private Double focusHours;

    @NotNull
    @FutureOrPresent
    @Column(name = "due_date")
    private LocalDate dueDate;

    // 🕒 New field for scheduling start time
    @Column(name = "start_time")
    private LocalTime startTime;

    // 🚩 New field for missed flag
    @Column(name = "missed", nullable = false)
    private boolean missed = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "completed", nullable = false)
    private boolean completed = false;

    @Column(name = "completed_date_time")
    private LocalDateTime completedDateTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "category")
    private String category;

    @Column(name = "icon_url")
    private String iconUrl;

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    // 🛠️ Constructors
    public Task() {}

    public Task(String title, String description, LocalDate dueDate, Priority priority, User user) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.user = user;
    }

    // ✅ Business Logic
    public void markCompleted() {
        this.completed = true;
        
        this.completedDateTime = LocalDateTime.now();
    }

    public void markUncompleted() {
        this.completed = false;
     
        this.completedDateTime = null;
    }

    // 🔁 Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public boolean isMissed() { return missed; }
    public void setMissed(boolean missed) { this.missed = missed; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getCompletedDateTime() { return completedDateTime; }
    public void setCompletedDateTime(LocalDateTime completedDateTime) { this.completedDateTime = completedDateTime; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    
    public Double getFocusHours() {
        return focusHours;
    }

    public void setFocusHours(Double focusHours) {
        this.focusHours = focusHours;
    }

    // 🔁 equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) &&
                Objects.equals(title, task.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }

    // 🧾 toString
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", dueDate=" + dueDate +
                ", startTime=" + startTime +
                ", missed=" + missed +
                ", priority=" + priority +
                ", completed=" + completed +
                '}';
    }
    
    public enum TaskCategory {
        HEALTH, PROJECT, STUDY, PERSONAL, OTHER
    }


	
}
