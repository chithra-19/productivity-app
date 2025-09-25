package com.climbup.dto.response;

public class GoalResponseDTO {

    private String title;
    private String description;
    private String deadline;       // formatted for UI, e.g., "2025-09-22"
    private boolean completed;     // true if goal is completed
    private boolean dropped;       // true if goal is dropped
    private int progressPercent;   // 0–100
    private String category;       // priority: LOW, MEDIUM, HIGH
    private String iconUrl;        // optional icon for UI

    // ===== No-args constructor =====
    public GoalResponseDTO() {}

    // ===== All-args constructor =====
    public GoalResponseDTO(String title, String description, String deadline,
                           boolean completed, boolean dropped, int progressPercent,
                           String category, String iconUrl) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.completed = completed;
        this.dropped = dropped;
        this.progressPercent = progressPercent;
        this.category = category;
        this.iconUrl = iconUrl;
    }

    // ===== Getters & Setters =====
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public boolean isDropped() { return dropped; }
    public void setDropped(boolean dropped) { this.dropped = dropped; }

    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    // ===== toString =====
    @Override
    public String toString() {
        return "GoalResponseDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", deadline='" + deadline + '\'' +
                ", completed=" + completed +
                ", dropped=" + dropped +
                ", progressPercent=" + progressPercent +
                ", category='" + category + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                '}';
    }
}
