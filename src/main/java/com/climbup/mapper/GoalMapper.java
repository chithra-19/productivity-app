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
        goal.setStatus(dto.getStatus());
        goal.setPriority(dto.getPriority());
        goal.setProgress(dto.getProgress());
        return goal;
    }

    // ===== Convert Goal entity → GoalResponseDTO =====
    public static GoalResponseDTO toDTO(Goal goal) {
        if (goal == null) return null;

        String deadline = goal.getDueDate() != null ? goal.getDueDate().format(DATE_FORMATTER) : null;

        boolean completed = goal.getStatus() == Goal.GoalStatus.COMPLETED;
        boolean dropped = goal.getStatus() == Goal.GoalStatus.DROPPED;

        return new GoalResponseDTO(
                goal.getTitle(),
                goal.getDescription(),
                deadline,
                completed,
                dropped,
                goal.getProgress(),
                goal.getPriority() != null ? goal.getPriority().name() : null,
                null  // iconUrl placeholder
        );
    }

    // ===== Update existing Goal entity from DTO =====
    public static void updateEntity(Goal goal, GoalRequestDTO dto) {
        if (goal == null || dto == null) return;

        goal.setTitle(dto.getTitle());
        goal.setDescription(dto.getDescription());
        goal.setDueDate(dto.getDueDate());
        goal.setStatus(dto.getStatus());
        goal.setPriority(dto.getPriority());
        goal.setProgress(dto.getProgress());
    }
}
