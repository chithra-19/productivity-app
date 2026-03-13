package com.climbup.dto.response;

public class GoalResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String dueDate;   // formatted for UI, e.g., "2026-03-15"
    private String priority;  // LOW, MEDIUM, HIGH
    private String status;    // ACTIVE, COMPLETED, etc.

    // ===== No-args constructor =====
    public GoalResponseDTO() {}

    // ===== All-args constructor =====
    public GoalResponseDTO(Long id, String title, String description,
                           String dueDate, String priority, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
    }

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "GoalResponseDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}