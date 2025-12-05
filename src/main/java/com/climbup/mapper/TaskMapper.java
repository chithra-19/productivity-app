package com.climbup.mapper;

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.request.TaskUpdateDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.model.Task;
import com.climbup.model.User;

/**
 * Mapper class for converting between Task DTOs and Task entity.
 * Keeps transformation logic centralized and reusable across layers.
 */
public class TaskMapper {

    /**
     * Converts a TaskRequestDTO and associated User into a Task entity.
     * Used during task creation.
     *
     * @param dto  incoming task data from client
     * @param user authenticated user creating the task
     * @return Task entity ready for persistence
     */
    public static Task toEntity(TaskRequestDTO dto, User user) {
        if (dto == null || user == null) return null;

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate()); // already LocalDate
        task.setPriority(dto.getPriority());
        task.setCategory(dto.getCategory());

        task.setUser(user); // establish ownership
        return task;
    }

    /**
     * Updates an existing Task entity using TaskUpdateDTO.
     * Used during task editing.
     *
     * @param task existing task entity
     * @param dto  update payload from client
     */
    public static void updateEntity(Task task, TaskUpdateDTO dto) {
        if (dto == null || task == null) return;

        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getDueDate() != null) task.setDueDate(dto.getDueDate());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());
        if (dto.getCategory() != null) task.setCategory(dto.getCategory()); // optional tag

     // Handle completion toggle
        if (dto.getCompleted() != null) {

            // Mark completed
            if (dto.getCompleted()) {

                // If task was NOT previously completed → set timestamps now
                if (!task.isCompleted() || task.getCompletedDateTime() == null) {
                    task.markCompleted();
                }

            } else {
                // Unmark completion
                task.setCompleted(false);
        
                task.setCompletedDateTime(null);
            }
        }

    }

    /**
     * Converts a Task entity into a TaskResponseDTO.
     * Used in GET responses to shape data for frontend.
     *
     * @param task persisted task entity
     * @return TaskResponseDTO for UI rendering
     */
    public static TaskResponseDTO toResponse(Task task) {
        if (task == null) return null;

        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.isCompleted(),
                task.getPriority() != null ? task.getPriority().name() : null,
                task.getCategory(),
                task.getIconUrl(),
                task.getCompletedDateTime()
        );
    }
}