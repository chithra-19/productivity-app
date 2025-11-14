package com.climbup.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for sending task data from backend to frontend.
 * Used in GET responses to shape task details for UI rendering.
 */
public class TaskResponseDTO {

    private Long id; // Unique task identifier

    private String title; // Task name/title

    private String description; // Optional notes or details

    private LocalDate dueDate; // Deadline for task completion

    private boolean completed; // Status flag

    private String priority; // LOW / MEDIUM / HIGH (stored as String for UI flexibility)

    private String category; // Optional grouping tag (e.g., Work, Personal)

    private String iconUrl; // Optional icon for visual representation in UI

    private LocalDate completionDate; // Date-only stamp for streaks or calendar

    private LocalDateTime completedDateTime; // Precise timestamp for analytics/logs

    // --- Constructors ---

    public TaskResponseDTO() {} // No-args constructor for serialization

    public TaskResponseDTO(Long id, String title, String description,
                           LocalDate dueDate, boolean completed,
                           String priority, String category, String iconUrl,
                           LocalDate completionDate, LocalDateTime completedDateTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
        this.priority = priority;
        this.category = category;
        this.iconUrl = iconUrl;
        this.completionDate = completionDate;
        this.completedDateTime = completedDateTime;
    }

    public TaskResponseDTO(Long id, String title, String description,
                           LocalDate dueDate, boolean completed,
                           String priority, String category, String iconUrl) {
        this(id, title, description, dueDate, completed, priority, category, iconUrl, null, null);
    }

    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public LocalDate getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }

    public LocalDateTime getCompletedDateTime() { return completedDateTime; }
    public void setCompletedDateTime(LocalDateTime completedDateTime) { this.completedDateTime = completedDateTime; }
}