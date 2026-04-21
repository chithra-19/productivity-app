package com.climbup.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private int progress = 0; // 0–100

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status = GoalStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    private LocalDate completedDate;
    
    @Column(nullable = false)
    private boolean completed = false;
  
    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAchievement> userAchievements = new ArrayList<>();

    public boolean getCompleted() {
		return completed;
	}


	public void setCompleted(boolean completed) {
		this.completed = completed;
	}


	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
	  
    public List<UserAchievement> getUserAchievements() {
		return userAchievements;
	}


	public void setUserAchievements(List<UserAchievement> userAchievements) {
		this.userAchievements = userAchievements;
	}


	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}


    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    
 
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

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
        if (this.status == GoalStatus.COMPLETED) {
            return;
        }

        this.status = GoalStatus.COMPLETED;
        this.progress = 100;
        this.completedDate = LocalDate.now();
        this.completed = true;
    }
    // ==== GETTERS & SETTERS ====
    public Long getId() { return id; }
    public void setId(Long id) {this.id= id;}

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

    public LocalDate getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

  

    @Override
    public String toString() {
        return "Goal{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", progress=" + progress +
                ", dueDate=" + dueDate +
                '}';
    }
}
