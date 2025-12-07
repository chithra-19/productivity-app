package com.climbup.model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "daily_stats")
public class DailyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private LocalDate date;

    private int taskCount;
    private int focusMinutes;
    

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
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public int getTaskCount() {
		return taskCount;
	}
	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}
	public int getFocusMinutes() {
		return focusMinutes;
	}
	public void setFocusMinutes(int focusMinutes) {
		this.focusMinutes = focusMinutes;
	}
}
