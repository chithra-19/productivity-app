package com.climbup.service.task;

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.request.TaskUpdateDTO;
import com.climbup.dto.response.HeatmapDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.mapper.TaskMapper;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.model.Activity.ActivityType;
import com.climbup.repository.TaskRepository;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.productivity.StreakTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    // --- Dependencies ---
    private final TaskRepository taskRepository;
    private final ActivityService activityService;
    private final StreakTrackerService streakTrackerService;
    private final AchievementService achievementService;

    @Autowired
    public TaskService(TaskRepository taskRepository,
                       ActivityService activityService,
                       StreakTrackerService streakTrackerService,
                       @Lazy AchievementService achievementService) {
        this.taskRepository = taskRepository;
        this.activityService = activityService;
        this.streakTrackerService = streakTrackerService;
        this.achievementService = achievementService;
    }

    // ➕ Create Task
    public TaskResponseDTO createTask(TaskRequestDTO dto, User user) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setUser(user);
        task.setCompleted(false);
        task.setCategory(dto.getCategory());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setPriority(dto.getPriority() != null ? dto.getPriority() : Task.Priority.MEDIUM);

        Task savedTask = taskRepository.save(task);
        activityService.log("Created Task: " + savedTask.getTitle(), ActivityType.TASK, user);

        return TaskMapper.toResponse(savedTask);
    }

    // 📋 Get all tasks for a user
    public List<TaskResponseDTO> getTasksForUser(User user) {
        return taskRepository.findByUser(user)
                .stream()
                .map(TaskMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ✏️ Update Task
    public TaskResponseDTO updateTask(Long taskId, TaskUpdateDTO dto, User user) {
        Task task = taskRepository.findByIdAndUser(taskId, user)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));

        Optional.ofNullable(dto.getTitle()).ifPresent(task::setTitle);
        Optional.ofNullable(dto.getDescription()).ifPresent(task::setDescription);
        Optional.ofNullable(dto.getDueDate()).ifPresent(task::setDueDate);
        Optional.ofNullable(dto.getPriority()).ifPresent(task::setPriority);
        Optional.ofNullable(dto.getCategory()).ifPresent(task::setCategory);

        if (dto.getCompleted() != null) {

            if (dto.getCompleted() && !task.isCompleted()) {

                task.setCompleted(true);
                task.setCompletedDateTime(LocalDateTime.now());

                // streak + achievements
                streakTrackerService.updateStreak(user);
                achievementService.checkForNewAchievements(user);

            } else if (!dto.getCompleted()) {
                task.setCompleted(false);
                task.setCompletedDateTime(null);
            }
        }

        Task updatedTask = taskRepository.save(task);
        activityService.log("Updated Task: " + updatedTask.getTitle(), ActivityType.TASK, user);

        return TaskMapper.toResponse(updatedTask);
    }

    // ❌ Delete Task
    public void deleteTask(Long taskId, User user) {
        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));

        taskRepository.delete(task);
        activityService.log("Deleted Task: " + task.getTitle(), ActivityType.TASK, user);
    }

    // ✅ Complete Task
    public TaskResponseDTO completeTask(Long taskId, User user) {

        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.isCompleted()) {
            task.setCompleted(true);
            task.setCompletedDateTime(LocalDateTime.now());
            taskRepository.save(task);

            activityService.log("Completed Task: " + task.getTitle(), ActivityType.TASK, user);
            streakTrackerService.updateStreak(user);
            achievementService.checkForNewAchievements(user);
        }

        return TaskMapper.toResponse(task);
    }


    // 🔹 Completed task count
    public long getCompletedTaskCount(User user) {
        return taskRepository.countByUserAndCompleted(user, true);
    }

    // 🔹 Focus hours by date
    public Map<LocalDate, Double> getFocusHoursByDate(List<Task> tasks) {
        return tasks.stream()
                .filter(Task::isCompleted)
                .filter(task -> task.getCompletedDateTime() != null)
                .collect(Collectors.groupingBy(
                        task -> task.getCompletedDateTime().toLocalDate(),   // FIXED 🔥
                        Collectors.summingDouble(
                                task -> task.getFocusHours() != null ? task.getFocusHours() : 0.0
                        )
                ));
    }


    public List<HeatmapDTO> getHeatmapData(User user) {

        List<Task> tasks = taskRepository.findByUserAndCompletedTrue(user);

        Map<LocalDate, List<Task>> grouped = tasks.stream()
                .filter(t -> t.getCompletedDateTime() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCompletedDateTime().toLocalDate()
                ));

        List<HeatmapDTO> result = new ArrayList<>();

        for (var entry : grouped.entrySet()) {

            LocalDate date = entry.getKey();
            List<Task> dayTasks = entry.getValue();

            int taskCount = dayTasks.size();

            int focusMinutes = dayTasks.stream()
                    .mapToInt(t -> t.getFocusHours() != null ? (int)(t.getFocusHours() * 60) : 0)
                    .sum();

            result.add(new HeatmapDTO(
                    date.toString(),
                    taskCount,
                    focusMinutes,
                    true
            ));
        }

        return result;
    }

    // 🔹 Get all tasks (dashboard)
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(TaskMapper::toResponse)
                .collect(Collectors.toList());
    }

    // 🔹 Get today’s tasks
    public List<TaskResponseDTO> getTodayTasks(User user) {
        LocalDate today = LocalDate.now();
        return taskRepository.findByUserAndDueDateBetween(
                        user, today, today) // Replace null with actual User when calling
                .stream()
                .map(TaskMapper::toResponse)
                .collect(Collectors.toList());
    }
    

    public Map<LocalDate, Long> getTaskStats(User user) {
        List<Task> tasks = taskRepository.findByUser(user);
        return tasks.stream()
                .collect(Collectors.groupingBy(
                        task -> task.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));
    }
    
    public Map<String, Integer> getHeatmapDataForUser(User user) {
        // Example: group tasks by date and count them
        return user.getTasks().stream()
            .filter(task -> task.getDueDate() != null)
            .collect(Collectors.groupingBy(
                task -> task.getDueDate().toString(), // format as yyyy-MM-dd
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));
    }

    public boolean markTaskAsCompleted(Long taskId, User user) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();

            if (task.getUser().getId().equals(user.getId())) {
                task.markCompleted();
                taskRepository.save(task);
                return true;
            }
        }
        return false;
    }

 // 🔹 Get today’s tasks for a user
    public List<Task> getTasksDueOn(User user, LocalDate date) {
        return taskRepository.findByUserAndDueDate(user, date);
    }

}
