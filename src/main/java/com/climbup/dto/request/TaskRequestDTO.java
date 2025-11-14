package com.climbup.dto.request;

import com.climbup.model.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * DTO for capturing task creation input from the client.
 * Used in POST/PUT requests to transfer task data from frontend to backend.
 */
public class TaskRequestDTO {

    @NotBlank(message = "Task title is required")
    private String title; // Core identifier for the task

    private String description; // Optional details or notes

    private LocalDate dueDate; // Deadline for task completion

    @NotNull(message = "Task priority is required")
    private Task.Priority priority; // Enum: LOW, MEDIUM, HIGH

    private String category; // Optional tag for grouping (e.g., Work, Personal)

    // --- Getters & Setters ---

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}