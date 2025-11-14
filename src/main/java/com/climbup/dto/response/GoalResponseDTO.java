package com.climbup.dto.response;

public class GoalResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String dueDate;       // formatted for UI, e.g., "2025-09-22"
    private boolean completed;     // true if goal is completed
    private boolean dropped;       // true if goal is dropped
    private int progressPercent;   // 0–100
    private String priority;       // priority: LOW, MEDIUM, HIGH
    private String iconUrl;        // optional icon for UI

    // ===== No-args constructor =====
    public GoalResponseDTO() {}

    // ===== All-args constructor =====
    public GoalResponseDTO(Long id, String title, String description, String dueDate,
                           boolean completed, boolean dropped, int progressPercent,
                           String priority, String iconUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
        this.dropped = dropped;
        this.progressPercent = progressPercent;
        this.priority = priority;
        this.iconUrl = iconUrl;
    }

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate= dueDate; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public boolean isDropped() { return dropped; }
    public void setDropped(boolean dropped) { this.dropped = dropped; }

    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    // ===== toString =====
    @Override
    public String toString() {
        return "GoalResponseDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate ='" + dueDate + '\'' +
                ", completed=" + completed +
                ", dropped=" + dropped +
                ", progressPercent=" + progressPercent +
                ", priority ='" + priority + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                '}';
    }
}
