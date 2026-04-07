package com.climbup.model;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "user_badges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBadge {


	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    private Instant earnedAt;
    
    
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

	public Badge getBadge() {
		return badge;
	}

	public void setBadge(Badge badge) {
		this.badge = badge;
	}

	public Instant getEarnedAt() {
		return earnedAt;
	}

	public void setEarnedAt(Instant earnedAt) {
		this.earnedAt = earnedAt;
	}

}
