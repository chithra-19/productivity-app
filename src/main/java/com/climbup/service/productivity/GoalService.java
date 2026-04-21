package com.climbup.service.productivity;

import com.climbup.dto.request.GoalRequestDTO;
import com.climbup.exception.ResourceNotFoundException;
import com.climbup.model.*;
import com.climbup.repository.AchievementTemplateRepository;
import com.climbup.repository.GoalRepository;
import com.climbup.repository.UserAchievementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final XPService xpService;
    private final AchievementEvaluationService achievementEvaluationService;
    private final UserAchievementRepository userAchievementRepository;
    private final AchievementTemplateRepository achievementTemplateRepository;

    public GoalService(GoalRepository goalRepository,
                       XPService xpService,
                       AchievementEvaluationService achievementEvaluationService,
                       UserAchievementRepository userAchievementRepository,
                       AchievementTemplateRepository achievementTemplateRepository) {
        this.goalRepository = goalRepository;
        this.xpService = xpService;
        this.achievementEvaluationService = achievementEvaluationService;
        this.userAchievementRepository = userAchievementRepository;
        this.achievementTemplateRepository = achievementTemplateRepository;
    }

    // ================= GET =================
    public List<Goal> filterGoals(User user, String status, String priority) {

        List<Goal> goals = goalRepository.findByUser(user);

        return goals.stream()
                .filter(g -> "ALL".equalsIgnoreCase(status)
                        || g.getStatus().name().equalsIgnoreCase(status))
                .filter(g -> "ALL".equalsIgnoreCase(priority)
                        || g.getPriority().name().equalsIgnoreCase(priority))
                .toList();
    }

    public List<Goal> getGoalsForUser(User user) {
        return goalRepository.findByUser(user);
    }

    // ================= CREATE =================
    @Transactional
    public Goal createGoal(GoalRequestDTO dto, User user) {
        Goal goal = new Goal();
        goal.setTitle(dto.getTitle());
        goal.setDescription(dto.getDescription());
        goal.setDueDate(dto.getDueDate());
        goal.setPriority(dto.getPriority());
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setProgress(0);
        goal.setCompleted(false);
        goal.setUser(user);

        Goal savedGoal = goalRepository.save(goal);

        // 🔹 Link achievement to this goal
        AchievementTemplate customTemplate =
        	    achievementTemplateRepository.findByCode(AchievementCode.CUSTOM_GOAL)
        	        .orElseThrow(() -> new ResourceNotFoundException("Template not found"));

        UserAchievement ua = new UserAchievement();
        ua.setUser(user);
        ua.setGoal(savedGoal);
        ua.setTemplate(customTemplate);
        ua.setUnlocked(false);
        ua.setNewlyUnlocked(false);
        ua.setSeen(false);
        ua.setDisplayTitle(savedGoal.getTitle()); // use goal title for display
        userAchievementRepository.save(ua);

        return savedGoal;
    }


    // ================= UPDATE =================
    @Transactional
    public Goal updateGoal(Long goalId, GoalRequestDTO dto, User user) {

        Goal goal = goalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        GoalStatus oldStatus = goal.getStatus();

        if (dto.getTitle() != null) goal.setTitle(dto.getTitle());
        if (dto.getDescription() != null) goal.setDescription(dto.getDescription());
        if (dto.getDueDate() != null) goal.setDueDate(dto.getDueDate());
        if (dto.getPriority() != null) goal.setPriority(dto.getPriority());
        if (dto.getStatus() != null) goal.setStatus(dto.getStatus());

        Goal updated = goalRepository.save(goal);

        if (oldStatus != GoalStatus.COMPLETED &&
                updated.getStatus() == GoalStatus.COMPLETED) {
            handleCompletion(updated, user);
            achievementEvaluationService.evaluate(user);
        }

        return updated;
    }

    // ================= COMPLETE GOAL =================
    @Transactional
    public Goal completeGoal(Long goalId, User user) {

        Goal goal = goalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (goal.getStatus() == GoalStatus.COMPLETED) {
            return goal;
        }

        goal.markCompleted();
        Goal savedGoal = goalRepository.save(goal);

        handleCompletion(savedGoal, user);

        achievementEvaluationService.evaluate(user);

        return savedGoal;
    }

    // ================= CORE COMPLETION LOGIC =================
    private void handleCompletion(Goal goal, User user) {

        // XP reward
        xpService.addXp(user, 50);

        // unlock achievements linked to this goal
        List<UserAchievement> achievements =
                userAchievementRepository.findByGoal(goal);

        for (UserAchievement ua : achievements) {
            if (!ua.isUnlocked()) {
                ua.unlock();
                ua.setNewlyUnlocked(true);
                userAchievementRepository.save(ua);
            }
        }
    }

    // ================= DELETE =================
    @Transactional
    public void deleteGoal(Long goalId, User user) {

        Goal goal = goalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        userAchievementRepository.deleteByGoalId(goalId);
        goalRepository.delete(goal);
    }
}