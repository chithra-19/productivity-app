package com.climbup.dto.request;

import com.climbup.model.Task;
import java.time.LocalDate;

public class TaskUpdateDTO {

    private String title;
    private String description;
    private LocalDate dueDate;
    private Task.Priority priority;
    private Boolean completed;  // nullable for partial updates

    // ===== Constructors =====
    public TaskUpdateDTO() {}

    public TaskUpdateDTO(String title, String description, LocalDate dueDate, Task.Priority priority, Boolean completed) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = completed;
    }

    // ===== Getters & Setters =====
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

    public Task.Priority getPriority() {
        return priority;
    }
    public void setPriority(Task.Priority priority) {
        this.priority = priority;
    }

    public Boolean getCompleted() {
        return completed;
    }
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    // ===== toString (optional) =====
    @Override
    public String toString() {
        return "TaskUpdateDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", completed=" + completed +
                '}';
    }
}
