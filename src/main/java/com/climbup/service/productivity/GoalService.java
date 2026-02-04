package com.climbup.service.productivity;

import com.climbup.exception.NotFoundException;
import com.climbup.exception.ResourceNotFoundException;
import com.climbup.model.Achievement;
import com.climbup.model.Achievement.AchievementCode;
import com.climbup.model.Goal;
import com.climbup.model.User;
import com.climbup.repository.AchievementRepository;
import com.climbup.repository.GoalRepository;
import com.climbup.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;

    @Autowired
    private AchievementService achievementService;

    public GoalService(GoalRepository goalRepository,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
    }

    // ===== Get all ACTIVE (not completed) goals for a user =====
    public List<Goal> getGoalsForUser(User user) {
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

    @Transactional
    public Goal saveGoal(Goal goal) {
        Goal saved = goalRepository.save(goal);

        
        // Evaluate all other achievements for this user
        achievementService.evaluateAchievements(saved.getUser());

        return saved;
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

    // ===== Mark goal as completed and unlock achievement =====
    @Transactional
    public Goal completeGoal(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Goal not found"));

        if (goal.isCompleted()) return goal;

        goal.setCompleted(true);
        goal.setStatus(Goal.GoalStatus.COMPLETED);
        goal.setProgress(100);
        goal.setCompletedDate(LocalDate.now());

        goalRepository.save(goal);

       
        // Evaluate all other achievements
        achievementService.evaluateAchievements(goal.getUser());

        return goal;
    }

   
    
    


}
