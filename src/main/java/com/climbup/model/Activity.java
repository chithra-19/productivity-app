package com.climbup.model;

import jakarta.persistence.*;

import java.time.Instant;
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

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ActivityType type;

    @Column(nullable = false)
    private Integer focusMinutes = 0;

 
    // Constructors
    public Activity() {}

    public Activity(String description, ActivityType type, User user) {
        this.description = description;
        this.type = type;
        this.user = user;
        this.timestamp = Instant.now();
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

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
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

	// Getter
	public ActivityType getType() {
	    return type;
	}

	// Setter
	public void setType(ActivityType type) {
	    this.type = type;
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

