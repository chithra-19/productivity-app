package com.climbup.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "activities", indexes = {
        @Index(name = "idx_user_timestamp", columnList = "user_id, timestamp")
})
public class Activity {


	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer focusMinutes = 0;

    public enum ActivityType {
        TASK, GOAL, CHALLENGE, FOCUS_SESSION, ACHIEVEMENT, LOGIN, OTHER, STREAK
    }

    // Constructors
    public Activity() {}

    public Activity(String description, ActivityType type, User user) {
        this.description = description;
        this.type = type;
        this.user = user;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ActivityType getType() {
		return type;
	}

	public void setType(ActivityType type) {
		this.type = type;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getFocusMinutes() {
		return focusMinutes;
	}

	public void setFocusMinutes(Integer focusMinutes) {
		this.focusMinutes = focusMinutes;
	}



    // equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Activity)) return false;
        Activity activity = (Activity) o;
        return Objects.equals(id, activity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
}

