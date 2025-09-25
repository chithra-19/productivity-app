package com.climbup.mapper;

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.request.TaskUpdateDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.model.Task;
import com.climbup.model.User;

public class TaskMapper {

    // 🔄 RequestDTO → Entity
    public static Task toEntity(TaskRequestDTO dto, User user) {
        if (dto == null || user == null) return null;

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());   // already LocalDate
        task.setPriority(dto.getPriority());
        task.setUser(user);
        return task;
    }

    // 🔄 UpdateDTO → Update existing Entity
    public static void updateEntity(Task task, TaskUpdateDTO dto) {
        if (dto == null || task == null) return;

        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getDueDate() != null) task.setDueDate(dto.getDueDate());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());

        if (dto.getCompleted() != null) {
            if (dto.getCompleted()) {
                task.markCompleted(); // sets completed + completionDate + completedDateTime
            } else {
                task.setCompleted(false);
                task.setCompletionDate(null);
                task.setCompletedDateTime(null);
            }
        }
    }

    // 🔄 Entity → ResponseDTO
    public static TaskResponseDTO toResponse(Task task) {
        if (task == null) return null;

        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),               // LocalDate
                task.isCompleted(),
                task.getPriority() != null ? task.getPriority().name() : null,
                task.getCategory(),              // optional
                task.getIconUrl(),               // optional
                task.getCompletionDate(),        // LocalDate
                task.getCompletedDateTime()      // LocalDateTime
        );
    }
}
