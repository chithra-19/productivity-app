package com.climbup.dto.request;

import com.climbup.model.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class TaskRequestDTO {

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;

    private LocalDate dueDate;

    @NotNull(message = "Task priority is required")
    private Task.Priority priority;

    // Getters & Setters
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
}
