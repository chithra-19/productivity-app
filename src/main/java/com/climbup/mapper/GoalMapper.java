package com.climbup.mapper;

import com.climbup.dto.request.GoalRequestDTO;
import com.climbup.dto.response.GoalResponseDTO;
import com.climbup.model.Goal;

import java.time.format.DateTimeFormatter;

public class GoalMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ===== Convert GoalRequestDTO → Goal entity =====
    public static Goal toEntity(GoalRequestDTO dto) {
        if (dto == null) return null;

        Goal goal = new Goal();
        goal.setTitle(dto.getTitle());
        goal.setDescription(dto.getDescription());
        goal.setDueDate(dto.getDueDate());
        goal.setPriority(dto.getPriority());
   
        return goal;
    }

    // ===== Convert Goal entity → GoalResponseDTO =====
    public static GoalResponseDTO toDTO(Goal goal) {
        if (goal == null) return null;

        String dueDate = goal.getDueDate() != null
                ? goal.getDueDate().format(DATE_FORMATTER)
                : null;

        String priority = goal.getPriority() != null
                ? goal.getPriority().name()
                : "MEDIUM";

        String status = goal.getStatus() != null
                ? goal.getStatus().name()
                : "ACTIVE";

        return new GoalResponseDTO(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                dueDate,
                priority,
                status
        );
    }

    // ===== Update existing Goal entity from DTO =====
    public static void updateEntity(Goal goal, GoalRequestDTO dto) {
        if (goal == null || dto == null) return;

        if (dto.getTitle() != null) goal.setTitle(dto.getTitle());
        if (dto.getDescription() != null) goal.setDescription(dto.getDescription());
        if (dto.getDueDate() != null) goal.setDueDate(dto.getDueDate());
        if (dto.getPriority() != null) goal.setPriority(dto.getPriority());
        if (dto.getStatus() != null) goal.setStatus(dto.getStatus());
    }
}