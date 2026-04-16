package com.climbup.controller.productivity;

import com.climbup.dto.request.GoalRequestDTO;
import com.climbup.dto.response.GoalResponseDTO;
import com.climbup.mapper.GoalMapper;
import com.climbup.model.Goal;
import com.climbup.model.User;
import com.climbup.service.productivity.GoalService;
import com.climbup.service.user.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;
    private final UserService userService;

    public GoalController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    private User currentUser() {
        return userService.getCurrentUser();
    }

    // 🔹 GET goals with filters
    @GetMapping
    public ResponseEntity<List<GoalResponseDTO>> getGoals(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "ALL") String priority
    ) {
        List<Goal> goals = goalService.filterGoals(currentUser(), status, priority);

        return ResponseEntity.ok(
                goals.stream().map(GoalMapper::toDTO).toList()
        );
    }

    // 🔹 CREATE goal
    @PostMapping
    public ResponseEntity<GoalResponseDTO> createGoal(
            @Valid @RequestBody GoalRequestDTO dto
    ) {
        Goal saved = goalService.createGoal(dto, currentUser());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GoalMapper.toDTO(saved));
    }

    // 🔹 UPDATE goal
    @PutMapping("/{goalId}")
    public ResponseEntity<GoalResponseDTO> updateGoal(
            @PathVariable Long goalId,
            @Valid @RequestBody GoalRequestDTO dto
    ) {
        Goal updated = goalService.updateGoal(goalId, dto, currentUser());

        return ResponseEntity.ok(GoalMapper.toDTO(updated));
    }

    // 🔹 COMPLETE goal
    @PutMapping("/{goalId}/complete")
    public ResponseEntity<GoalResponseDTO> completeGoal(@PathVariable Long goalId) {

        Goal goal = goalService.completeGoalAndReturn(goalId, currentUser());

        return ResponseEntity.ok(GoalMapper.toDTO(goal));
    }

    // 🔹 DELETE goal
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId) {

        goalService.deleteGoal(goalId, currentUser());

        return ResponseEntity.noContent().build();
    }
}