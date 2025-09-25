package com.climbup.service.productivity;

import com.climbup.exception.NotFoundException;
import com.climbup.exception.ResourceNotFoundException;
import com.climbup.model.Goal;
import com.climbup.model.User;
import com.climbup.repository.GoalRepository;
import com.climbup.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalService(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    // ===== Get all goals for a user =====
    public List<Goal> getGoalsByUser(User user) {
        return goalRepository.findByUser(user);
    }

    // ===== Filter goals by status and priority =====
    public List<Goal> filterGoals(User user, String status, String priority) {
        List<Goal> allGoals = goalRepository.findByUser(user);

        return allGoals.stream()
                .filter(goal -> "ALL".equalsIgnoreCase(status) || goal.getStatus().name().equalsIgnoreCase(status))
                .filter(goal -> "ALL".equalsIgnoreCase(priority) || goal.getPriority().name().equalsIgnoreCase(priority))
                .toList();
    }

    // ===== Get goal by ID =====
    public Goal getGoalById(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Goal not found with ID: " + id));
    }

    // ===== Save a new goal =====
    public Goal saveGoal(Goal goal) {
        return goalRepository.save(goal);
    }

    // ===== Update an existing goal =====
    public Goal updateGoal(Long id, Goal updatedGoal) {
        Goal existingGoal = getGoalById(id);

        if (updatedGoal.getTitle() != null) existingGoal.setTitle(updatedGoal.getTitle());
        if (updatedGoal.getDescription() != null) existingGoal.setDescription(updatedGoal.getDescription());
        if (updatedGoal.getDueDate() != null) existingGoal.setDueDate(updatedGoal.getDueDate());
        if (updatedGoal.getPriority() != null) existingGoal.setPriority(updatedGoal.getPriority());
        if (updatedGoal.getStatus() != null) existingGoal.setStatus(updatedGoal.getStatus());
        if (updatedGoal.getProgress() >= 0 && updatedGoal.getProgress() <= 100) {
            existingGoal.setProgress(updatedGoal.getProgress());
        }

        return goalRepository.save(existingGoal);
    }

    // ===== Delete a goal =====
    public void deleteGoal(Long id) {
        if (!goalRepository.existsById(id)) {
            throw new NotFoundException("Goal not found with ID: " + id);
        }
        goalRepository.deleteById(id);
    }

    // ===== Get goal by ID and username (ownership check) =====
    public Goal getGoalByIdAndUser(Long goalId, String username) {
        return goalRepository.findByIdAndUser_Username(goalId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
    }
}
