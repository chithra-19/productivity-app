package com.climbup.service.productivity;

import java.util.List;

import org.springframework.stereotype.Service;

import com.climbup.dto.request.AchievementRequestDTO;
import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.mapper.AchievementMapper;
import com.climbup.model.Achievement;
import com.climbup.model.Achievement.AchievementCode;
import com.climbup.model.Goal;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.AchievementRepository;
import com.climbup.repository.GoalRepository;
import com.climbup.repository.TaskRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import jakarta.transaction.Transactional;
@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final GoalRepository goalRepository;
    private final TaskRepository taskRepository;

    public AchievementService(AchievementRepository achievementRepository,
                              GoalRepository goalRepository,
                              TaskRepository taskRepository) {
        this.achievementRepository = achievementRepository;
        this.goalRepository = goalRepository;
        this.taskRepository = taskRepository;
    }

    // ================= GET USER ACHIEVEMENTS =================
    public List<Achievement> getUserAchievements(User user) {
        return achievementRepository.findByUser(user);
    }

    // ================= INITIAL SEEDING =================
    @Transactional
    public void initializeAchievements(User user) {
        if (achievementRepository.countByUser(user) > 0) return;

        achievementRepository.saveAll(List.of(
                create(user, AchievementCode.FIRST_STEP,
                        "First Step", "Complete your first task", Achievement.Type.TASK, "bi-check-circle"),

                create(user, AchievementCode.GOAL_1,
                        "Goal Setter", "Complete 1 goal", Achievement.Type.GOAL, "bi-flag"),

                create(user, AchievementCode.GOAL_5,
                        "Goal Grinder", "Complete 5 goals", Achievement.Type.GOAL, "bi-trophy"),

                create(user, AchievementCode.GOAL_10,
                        "Goal Master", "Complete 10 goals", Achievement.Type.GOAL, "bi-award"),

                create(user, AchievementCode.TASK_MASTER,
                        "Task Master", "Complete 10 tasks", Achievement.Type.TASK, "bi-lightning"),

                create(user, AchievementCode.EARLY_BIRD,
                        "Early Bird", "Complete a task before 8 AM", Achievement.Type.TASK, "bi-sun")
        ));
    }

    private Achievement create(User user,
                               AchievementCode code,
                               String title,
                               String description,
                               Achievement.Type type,
                               String icon) {

        Achievement a = new Achievement();
        a.setUser(user);
        a.setCode(code);
        a.setTitle(title);
        a.setDescription(description);
        a.setType(type);
        a.setIcon(icon);
        return a;
    }

    // ================= EVALUATE ACHIEVEMENTS =================
    @Transactional
    public void evaluateAchievements(User user) {

        long completedGoals = goalRepository.findByUser(user)
                .stream().filter(Goal::isCompleted).count();

        long completedTasks = taskRepository.findByUser(user)
                .stream().filter(Task::isCompleted).count();

        unlockIfEligible(user, AchievementCode.GOAL_1, completedGoals >= 1);
        unlockIfEligible(user, AchievementCode.GOAL_5, completedGoals >= 5);
        unlockIfEligible(user, AchievementCode.GOAL_10, completedGoals >= 10);

        unlockIfEligible(user, AchievementCode.FIRST_STEP, completedTasks >= 1);
        unlockIfEligible(user, AchievementCode.TASK_MASTER, completedTasks >= 10);

        boolean earlyBird = taskRepository.findByUserAndCompletedTrue(user)
                .stream()
                .anyMatch(t -> t.getCompletedDateTime() != null &&
                        t.getCompletedDateTime().getHour() < 8);

        unlockIfEligible(user, AchievementCode.EARLY_BIRD, earlyBird);
    }

    private void unlockIfEligible(User user, AchievementCode code, boolean condition) {
        if (!condition) return;

        Achievement achievement = achievementRepository
                .findByUserAndCode(user, code)
                .orElseThrow(() ->
                        new IllegalStateException("Achievement not initialized: " + code));

        if (!achievement.isUnlocked()) {
            achievement.unlock();
            achievementRepository.save(achievement);
        }
    }

    // ================= MARK AS SEEN =================
    @Transactional
    public void markAchievementsAsSeen(User user) {
        List<Achievement> achievements =
                achievementRepository.findByUserAndNewlyUnlockedTrue(user);

        achievements.forEach(Achievement::markSeen);
        achievementRepository.saveAll(achievements);
    }

    

    @Transactional
    public AchievementResponseDTO createAchievement(AchievementRequestDTO dto, User user) {
        Achievement achievement = new Achievement();
        achievement.setTitle(dto.getTitle());
        achievement.setDescription(dto.getDescription());
        achievement.setType(dto.getType());
        achievement.setCategory(dto.getCategory());
        if (dto.getUnlockedDate() != null) {
            achievement.setUnlockedAt(dto.getUnlockedDate().atStartOfDay());
        }

        achievement.setUser(user);

        achievementRepository.save(achievement);

        return AchievementMapper.toResponseDTO(achievement);
    }


    public boolean checkForNewAchievements(User user) {
        return !achievementRepository.findByUserAndNewlyUnlockedTrue(user).isEmpty();
    }
    
    @Transactional
    public AchievementResponseDTO unlockAchievement(Long achievementId, User user) {
        Achievement achievement = achievementRepository.findById(achievementId)

        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Achievement not found with ID: " + achievementId));

        // Optional: verify that this achievement belongs to the user
        if (!achievement.getUser().equals(user)) {
            throw new IllegalStateException("User does not own this achievement");
        }

        if (!achievement.isUnlocked()) {
            achievement.unlock();
            achievementRepository.save(achievement);
        }

        return AchievementMapper.toResponseDTO(achievement);
    }

}
