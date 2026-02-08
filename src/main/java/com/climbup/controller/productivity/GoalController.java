package com.climbup.controller.productivity;

import com.climbup.dto.request.GoalRequestDTO;
import com.climbup.dto.response.GoalResponseDTO;
import com.climbup.mapper.GoalMapper;
import com.climbup.model.Goal;
import com.climbup.model.User;
import com.climbup.repository.UserRepository;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.productivity.GoalService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;
    private final UserRepository userRepository;
    private final AchievementService achievementService;

    public GoalController(GoalService goalService, UserRepository userRepository, AchievementService achievementService) {
        this.goalService = goalService;
        this.userRepository = userRepository;
        this.achievementService = achievementService;
    }

    // ✅ Utility: Get authenticated user
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ GET /goals — Filter goals by status and priority
    @GetMapping
    public ResponseEntity<List<GoalResponseDTO>> getGoals(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "ALL") String priority
    ) {
        User user = getCurrentUser();
        List<Goal> goals = goalService.filterGoals(user, status, priority);
        List<GoalResponseDTO> dtoList = goals.stream()
                .map(GoalMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    // ✅ POST /goals/save — Create a new goal
    @PostMapping("/save")
    public ResponseEntity<GoalResponseDTO> saveGoal(@RequestBody GoalRequestDTO dto) {
        User user = getCurrentUser();
        Goal goal = GoalMapper.toEntity(dto);
        goal.setUser(user);
        Goal saved = goalService.saveGoal(goal);
        return ResponseEntity.status(HttpStatus.CREATED).body(GoalMapper.toDTO(saved));
    }


    // ✅ PUT /goals/{goalId} — Update an existing goal
    @PutMapping("/{goalId}")
    public ResponseEntity<GoalResponseDTO> updateGoal(
            @PathVariable Long goalId,
            @RequestBody GoalRequestDTO dto
    ) {
        User user = getCurrentUser();
        Goal existingGoal = goalService.getGoalByIdAndUser(goalId, user.getUsername());

        // ✅ Defensive null check
        if (existingGoal == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // ✅ Selective field updates
        if (dto.getTitle() != null) existingGoal.setTitle(dto.getTitle());
        if (dto.getDescription() != null) existingGoal.setDescription(dto.getDescription());
        if (dto.getDueDate() != null) existingGoal.setDueDate(dto.getDueDate());
        if (dto.getPriority() != null) existingGoal.setPriority(dto.getPriority());
        if (dto.getStatus() != null) existingGoal.setStatus(dto.getStatus());
        if (dto.getProgress() >= 0 && dto.getProgress() <= 100) {
            existingGoal.setProgress(dto.getProgress());
        }

        Goal saved = goalService.updateGoal(goalId, existingGoal);
        return ResponseEntity.ok(GoalMapper.toDTO(saved));
    }

 // ✅ PUT /goals/{goalId}/complete — Mark goal as completed
    @PutMapping("/{goalId}/complete")
    public ResponseEntity<GoalResponseDTO> completeGoal(@PathVariable Long goalId) {
        User user = getCurrentUser();

        // Validate ownership
        Goal goal = goalService.getGoalByIdAndUser(goalId, user.getUsername());
        if (goal == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // ✅ Call completeGoal correctly
        goalService.completeGoal(goal, user);

        // Return updated goal
        return ResponseEntity.ok(GoalMapper.toDTO(goal));
    }

    // ✅ PUT /goals/{goalId}/drop — Drop a goal
    @PutMapping("/{goalId}/drop")
    public ResponseEntity<GoalResponseDTO> dropGoal(@PathVariable Long goalId) {
        User user = getCurrentUser();
        Goal goal = goalService.getGoalByIdAndUser(goalId, user.getUsername());

        if (goal == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        goal.dropGoal();
        Goal saved = goalService.updateGoal(goalId, goal);
        return ResponseEntity.ok(GoalMapper.toDTO(saved));
    }

    // ✅ DELETE /goals/{goalId} — Delete a goal
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId) {
        User user = getCurrentUser();
        Goal goal = goalService.getGoalByIdAndUser(goalId, user.getUsername());

        if (goal == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        goalService.deleteGoal(goal.getId());
        return ResponseEntity.noContent().build();
    }
    
    
}