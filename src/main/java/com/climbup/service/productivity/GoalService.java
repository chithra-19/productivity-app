package com.climbup.service.productivity;

import com.climbup.dto.request.GoalRequestDTO;
import com.climbup.exception.NotFoundException;
import com.climbup.exception.ResourceNotFoundException;
import com.climbup.mapper.GoalMapper;
import com.climbup.model.*;
import com.climbup.repository.GoalRepository;
import com.climbup.repository.UserAchievementRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final XPService xpService;
    private final AchievementService achievementService;
    private final UserAchievementRepository userAchievementRepository;
    private final AchievementEvaluationService achievementEvaluationService;

    public GoalService(GoalRepository goalRepository,
                       XPService xpService,
                       AchievementService achievementService,
                       UserAchievementRepository userAchievementRepository,
                       AchievementEvaluationService achievementEvaluationService) {
        this.goalRepository = goalRepository;
        this.xpService = xpService;
        this.achievementService = achievementService;
        this.userAchievementRepository = userAchievementRepository;
        this.achievementEvaluationService = achievementEvaluationService;
    }

    // ===== Get goals =====
    public List<Goal> filterGoals(User user, String status, String priority) {
        return goalRepository.findByUser(user).stream()
                .filter(goal -> "ALL".equalsIgnoreCase(status) || goal.getStatus().name().equalsIgnoreCase(status))
                .filter(goal -> "ALL".equalsIgnoreCase(priority) || goal.getPriority().name().equalsIgnoreCase(priority))
                .toList();
    }

 // ===== Create =====
    @Transactional
    public Goal createGoal(GoalRequestDTO dto, User user) {

        Goal goal = new Goal();

        goal.setTitle(dto.getTitle());
        goal.setDescription(dto.getDescription());
        goal.setDueDate(dto.getDueDate());

        goal.setStatus(dto.getStatus() != null ? dto.getStatus() : GoalStatus.ACTIVE);
        goal.setPriority(dto.getPriority() != null ? dto.getPriority() : Goal.Priority.MEDIUM);

        goal.setProgress(0);
        goal.setCompleted(false);
        goal.setUser(user);

        // save the goal first
        Goal savedGoal = goalRepository.save(goal);

        // 🔥 create a locked achievement linked to this goal
        AchievementTemplate template = achievementService.findTemplateByName(savedGoal.getTitle());
        if (template != null) {
            UserAchievement ua = new UserAchievement();
            ua.setUser(user);
            ua.setTemplate(template);
            ua.setGoal(savedGoal);
            ua.setUnlocked(false);
            userAchievementRepository.save(ua);
        }

        return savedGoal;
    }


    // ===== Update =====
    @Transactional
    public Goal updateGoal(Long goalId, GoalRequestDTO dto, User user) {

        Goal existing = goalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        GoalStatus oldStatus = existing.getStatus();

        if (dto.getTitle() != null) existing.setTitle(dto.getTitle());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getDueDate() != null) existing.setDueDate(dto.getDueDate());
        if (dto.getPriority() != null) existing.setPriority(dto.getPriority());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());

        Goal saved = goalRepository.save(existing);

        // 🔥 trigger achievement only on completion
        if (oldStatus != GoalStatus.COMPLETED && saved.getStatus() == GoalStatus.COMPLETED) {
            achievementEvaluationService.evaluate(user);
        }

        return saved;
    }

 // ===== Complete =====
    @Transactional
    public void completeGoal(Long goalId, User user) {

        Goal goal = goalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (goal.getStatus() == GoalStatus.COMPLETED) {
            return; // already completed, nothing to do
        }

        // mark goal complete
        goal.markCompleted();
        goalRepository.save(goal);

        // 🔥 unlock linked achievements
        List<UserAchievement> achievements = userAchievementRepository.findByGoal(goal);
        for (UserAchievement ua : achievements) {
            if (!ua.isUnlocked()) {
                ua.unlock();
                userAchievementRepository.save(ua);
            }
        }

        // award XP
        xpService.addXp(user, 50);

        // evaluate other achievement conditions (e.g., streaks, totals)
        achievementEvaluationService.evaluate(user);
    }


    // ===== Delete =====
    @Transactional
    public void deleteGoal(Long goalId, User user) {

        Goal goal = goalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        // 🔥 delete children first
        userAchievementRepository.deleteByGoalId(goalId);

        goalRepository.delete(goal);
    }
    
    public List<Goal> getGoalsForUser(User user) {
        return goalRepository.findByUser(user);
    }
    public Goal completeGoalAndReturn(Long id, User user) {
    	Goal goal = goalRepository.findByIdAndUser(id, user)
    	        .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (goal.getStatus() == GoalStatus.COMPLETED) return goal;

        goal.markCompleted();
        goalRepository.save(goal);

        xpService.addXp(user, 50);
        achievementEvaluationService.evaluate(user);

        return goal;
    }
}