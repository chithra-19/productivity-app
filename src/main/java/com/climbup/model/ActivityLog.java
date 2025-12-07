package com.climbup.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")
public class ActivityLog {


	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // e.g. TASK_COMPLETED, STREAK_UPDATED, ACHIEVEMENT_UNLOCKED
    @Column(nullable = false)
    private String type;

	private Integer taskCount = 0;      // 🔥 add this
    private Integer focusMinutes = 0;  
  

	@Column(nullable = true)
    private String category;


    // Human-friendly text: "Completed task: Build Profile Page"
    @Column(nullable = false, length = 255)
    private String description;

    // Date when activity happened
    @Column(nullable = false)
    private LocalDate activityDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime loggedAt;

    // --- getters & setters ---
    
    

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public LocalDateTime getLoggedAt() {
		return loggedAt;
	}

	public void setLoggedAt(LocalDateTime loggedAt) {
		this.loggedAt = loggedAt;
	}

	  public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
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

}
