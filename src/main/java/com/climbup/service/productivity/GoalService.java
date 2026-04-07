package com.climbup.service.productivity;

import com.climbup.exception.NotFoundException;
import com.climbup.exception.ResourceNotFoundException;
import com.climbup.model.Achievement;
import com.climbup.model.Achievement.AchievementCode;
import com.climbup.model.Goal;
import com.climbup.model.Goal.Priority;
import com.climbup.model.GoalStatus;
import com.climbup.model.User;
import com.climbup.repository.AchievementRepository;
import com.climbup.repository.GoalRepository;
import com.climbup.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final XPService xpservice;
    @Autowired
    private AchievementService achievementService;

    public GoalService(GoalRepository goalRepository,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository,
                       XPService xpservice) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.xpservice = xpservice;
    }

    // ===== Get all ACTIVE (not completed) goals for a user =====
    public List<Goal> getGoalsForUser(User user) {
        return goalRepository.findByUser(user);
    }

    // ===== Filter goals by status and priority =====
    public List<Goal> filterGoals(User user, String status, String priority) {
        List<Goal> allGoals = goalRepository.findByUser(user);
        if (allGoals == null) allGoals = new ArrayList<>();

        // Ensure every goal has non-null status & priority
        allGoals.forEach(goal -> {
            if (goal.getStatus() == null) goal.setStatus(GoalStatus.ACTIVE);
            if (goal.getPriority() == null) goal.setPriority(Priority.MEDIUM); // or your default
        });

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
        // ✅ Get the logged-in user
        User user = goal.getUser(); // may be set in controller
        if (user == null) {
            throw new RuntimeException("Goal user is null! Did you set it in controller?");
        }

        // ✅ Ensure user ID is present
        if (user.getId() == null) {
            System.out.println("Current user ID is null! user email: " + user.getEmail());
        } else {
            System.out.println("Saving goal for user ID: " + user.getId() + " email: " + user.getEmail());
        }

        // ✅ Ensure status & priority defaults
        if (goal.getStatus() == null) goal.setStatus(GoalStatus.ACTIVE);
        if (goal.getPriority() == null) goal.setPriority(Priority.MEDIUM);

        // ✅ Save goal
        Goal saved = goalRepository.save(goal);
        Achievement achievement = new Achievement();
        achievement.setUser(user);
        achievement.setGoal(saved);
        //achievement.setCode(AchievementCode.GOAL_COMPLETED); // or generate unique code
        achievement.setTitle("Complete Goal: " + saved.getTitle());
        achievement.setDescription("Finish the goal: " + saved.getTitle());
        achievement.setType(Achievement.Type.GOAL);
        achievement.setIcon("bi-flag");
        achievement.setUnlocked(false);
        achievement.setNewlyUnlocked(false);

        achievementRepository.save(achievement);
        achievementService.seedAchievementsForGoal(saved);
        
        // Evaluate achievements
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
    public Goal getGoalByIdAndUser(Long goalId, User user) {
        return goalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
    }

    // ===== Mark goal as completed and unlock achievement =====
    @Transactional
    public void completeGoal(Goal goal, User user) {

        if (goal.getStatus() == GoalStatus.COMPLETED) {
            return; // prevent double completion
        }

        goal.markCompleted();  // use entity business logic
        goalRepository.save(goal);

        xpservice.addXp(user, 50);

        achievementService.evaluateAchievements(user);
        achievementRepository.findByUserAndGoal(user, goal)
        .ifPresent(achievement -> {
            if (!achievement.isUnlocked()) {
                achievement.unlock(); // sets unlocked = true, newlyUnlocked = true, timestamps
                achievementRepository.save(achievement);
            }
        });
    }
    
    

}
