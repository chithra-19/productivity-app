package com.climbup.dto.response;

import java.time.Instant;
import java.time.LocalDate;

/**
 * DTO for sending task data from backend to frontend.
 */
public class TaskResponseDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private boolean completed;
    private String priority;
    private String category;
    private String iconUrl;
    private int xpEarned;
    private Instant completedDateTime; // 🔥 FIXED

    // --- Constructors ---

    public TaskResponseDTO() {}

    public TaskResponseDTO(Long id, String title, String description,
                           LocalDate dueDate, boolean completed,
                           String priority, String category, String iconUrl,
                           int xpEarned,
                           Instant completedDateTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
        this.priority = priority;
        this.category = category;
        this.iconUrl = iconUrl;
        this.xpEarned = xpEarned;
        this.completedDateTime = completedDateTime;
    }

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getXpEarned() {
        return xpEarned;
    }

    public void setXpEarned(int xpEarned) {
        this.xpEarned = xpEarned;
    }

    public Instant getCompletedDateTime() {
        return completedDateTime;
    }

    public void setCompletedDateTime(Instant completedDateTime) {
        this.completedDateTime = completedDateTime;
    }
}