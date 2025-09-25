package com.climbup.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskResponseDTO {

    private Long id;                 // task ID for frontend
    private String title;
    private String description;
    private LocalDate dueDate;       // due date of task
    private boolean completed;
    private String priority;         // LOW / MEDIUM / HIGH
    private String category;         // optional
    private String iconUrl;          // optional for UI
    private LocalDate completionDate;      // date when task was completed
    private LocalDateTime completedDateTime; // timestamp when task was completed

    // 🛠️ No-args constructor
    public TaskResponseDTO() {}

    // 🛠️ Full constructor (all fields)
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

    // 🛠️ Overloaded constructor (without completion fields)
    public TaskResponseDTO(Long id, String title, String description,
                           LocalDate dueDate, boolean completed,
                           String priority, String category, String iconUrl) {
        this(id, title, description, dueDate, completed, priority, category, iconUrl, null, null);
    }

    // ✅ Getters and Setters
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
