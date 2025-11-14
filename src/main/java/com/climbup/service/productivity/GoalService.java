package com.climbup.service.productivity;

import com.climbup.exception.NotFoundException;
import com.climbup.exception.ResourceNotFoundException;
import com.climbup.model.Achievement;
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
    public List<Goal> getGoalsByUser(User user) {
        return goalRepository.findByUserAndCompletedFalse(user);
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

    // ===== Mark goal as completed and unlock achievement =====
    @Transactional
    public Goal completeGoal(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Goal not found with ID: " + id));

        goal.setCompleted(true);
        goal.setStatus(Goal.GoalStatus.COMPLETED);
        goal.setProgress(100);

        goalRepository.save(goal);

        // ✅ Centralized check
        achievementService.checkForNewAchievements(goal.getUser());

        return goal;
    }

    // ===== Unlock achievement for first goal completion =====
    @Transactional
    public void unlockAchievementForGoalCompletion(User user, Goal goal) {
        // Skip if not completed
        if (!goal.isCompleted()) return;

        boolean alreadyUnlocked = achievementRepository
        	    .existsByUserAndTitle(user, goal.getTitle());

        	if (!alreadyUnlocked) {
        	    Achievement achievement = new Achievement();
        	    achievement.setTitle(goal.getTitle());
        	    achievement.setDescription("Unlocked by completing goal: " + goal.getTitle());
        	    achievement.setUser(user);
        	    achievement.setUnlocked(true);
        	    achievement.setNewlyUnlocked(true);
        	    achievement.setUnlockedAt(LocalDateTime.now());
        	    achievementRepository.save(achievement);
        	}
    }
    
    @Transactional
    public void markGoalAsCompleted(Long goalId, User user) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Goal not found"));

        if (!goal.getUser().equals(user)) {
            throw new SecurityException("Goal does not belong to user");
        }

        // mark as completed
        goal.setCompleted(true);
        goal.setDropped(false);
        goal.setStatus(Goal.GoalStatus.COMPLETED);
        goal.setProgress(100);

        goalRepository.save(goal); // ✅ make sure this is here

        // 🔥 trigger achievement check
        achievementService.checkForNewAchievements(user);
    }


}
