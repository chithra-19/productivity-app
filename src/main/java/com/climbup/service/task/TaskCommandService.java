package com.climbup.service.task;

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.request.TaskUpdateDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.mapper.TaskMapper;
import com.climbup.model.ActivityType;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.TaskRepository;
import com.climbup.repository.UserRepository;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.productivity.ProductivityService;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.productivity.XPService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class TaskCommandService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ActivityService activityService;
    private final XPService xpService;
    private final StreakTrackerService streakTrackerService;
    private final AchievementService achievementService;
    private final ProductivityService productivityService;

    public TaskCommandService(TaskRepository taskRepository,
                              UserRepository userRepository,
                              ActivityService activityService,
                              XPService xpService,
                              StreakTrackerService streakTrackerService,
                              AchievementService achievementService,
                              ProductivityService productivityService) {

        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.activityService = activityService;
        this.xpService = xpService;
        this.streakTrackerService = streakTrackerService;
        this.achievementService = achievementService;
        this.productivityService = productivityService;
    }

    // =========================================================
    // CREATE TASK
    // =========================================================
    public TaskResponseDTO createTask(TaskRequestDTO dto, User user) {

        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setCategory(dto.getCategory());
        task.setDueDate(dto.getDueDate());
        task.setPriority(dto.getPriority() != null
                ? dto.getPriority()
                : Task.Priority.MEDIUM);

        task.setUser(user);
        task.setCompleted(false);
        task.setTaskDate(LocalDate.now());

        Task savedTask = taskRepository.save(task);

        activityService.log(
                "Created task: " + savedTask.getTitle(),
                ActivityType.TASK_CREATED,
                user
        );

        return TaskMapper.toResponse(savedTask, getPriorityPoints(savedTask));
    }

    // =========================================================
    // UPDATE TASK
    // =========================================================
    public TaskResponseDTO updateTask(Long taskId, TaskUpdateDTO dto, User user) {

        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        Optional.ofNullable(dto.getTitle()).ifPresent(task::setTitle);
        Optional.ofNullable(dto.getDescription()).ifPresent(task::setDescription);
        Optional.ofNullable(dto.getCategory()).ifPresent(task::setCategory);
        Optional.ofNullable(dto.getDueDate()).ifPresent(task::setDueDate);
        Optional.ofNullable(dto.getPriority()).ifPresent(task::setPriority);

        if (Boolean.FALSE.equals(dto.getCompleted())) {
            task.setCompleted(false);
            task.setCompletedDateTime(null);
        }

        Task updated = taskRepository.save(task);

        return TaskMapper.toResponse(updated, getPriorityPoints(updated));
    }

    // =========================================================
    // DELETE TASK
    // =========================================================
    public void deleteTask(Long taskId, User user) {

        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        taskRepository.delete(task);

        
    }

    // =========================================================
    // MARK TASK AS DONE (GAMIFICATION CORE)
    // =========================================================
    public TaskResponseDTO markDone(Long taskId, User user) {

        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.isCompleted()) {
            throw new IllegalStateException("Task already completed");
        }

        task.setCompleted(true);
        task.setCompletedDateTime(Instant.now());

        if (task.getTaskDate() == null) {
            task.setTaskDate(LocalDate.now());
        }

        Task saved = taskRepository.save(task);

        int xp = getPriorityPoints(saved);

        // =========================
        // GAMIFICATION TRIGGERS
        // =========================

        activityService.logTaskCompleted(saved, user);

        xpService.handleTaskCompletion(user, xp);

        streakTrackerService.refreshUserStreak(user);

        achievementService.evaluate(user);

        int productivity = productivityService.calculate(user);
        user.setProductivityScore(productivity);

        userRepository.save(user);

        return TaskMapper.toResponse(saved, xp);
    }

    // =========================================================
    // PRIORITY POINTS
    // =========================================================
    private int getPriorityPoints(Task task) {

        return switch (task.getPriority()) {
            case LOW -> 1;
            case MEDIUM -> 3;
            case HIGH -> 5;
        };
    }
}