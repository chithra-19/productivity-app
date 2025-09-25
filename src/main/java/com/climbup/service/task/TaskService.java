package com.climbup.service.task;

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.request.TaskUpdateDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.mapper.TaskMapper;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.model.Activity.ActivityType;
import com.climbup.repository.TaskRepository;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.productivity.StreakTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ActivityService activityService;
    private final StreakTrackerService streakTrackerService;
    private final AchievementService achievementService;

    @Autowired
    public TaskService(TaskRepository taskRepository,
                       ActivityService activityService,
                       StreakTrackerService streakService,
                       AchievementService achievementService) {
        this.taskRepository = taskRepository;
        this.activityService = activityService;
        this.streakTrackerService = streakService;
        this.achievementService = achievementService;
    }

    // ➕ Create a new task
    public TaskResponseDTO createTask(TaskRequestDTO dto, User user) {
        Task task = TaskMapper.toEntity(dto, user);
        task.setCompleted(false);
        Task savedTask = taskRepository.save(task);

        activityService.log("Created Task: " + savedTask.getTitle(), ActivityType.TASK, savedTask.getUser());
        return TaskMapper.toResponse(savedTask);
    }

    // 📋 Get all tasks for a specific user
    public List<TaskResponseDTO> getTasksForUser(User user) {
        return taskRepository.findByUser(user)
                .stream()
                .map(TaskMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ✏️ Update a task
    public TaskResponseDTO updateTask(Long taskId, TaskUpdateDTO dto, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));

        if (!task.getUser().equals(user)) {
            throw new SecurityException("You can only update your own tasks!");
        }

        TaskMapper.updateEntity(task, dto);
        Task updatedTask = taskRepository.save(task);

        activityService.log("Updated Task: " + updatedTask.getTitle(), ActivityType.TASK, updatedTask.getUser());
        return TaskMapper.toResponse(updatedTask);
    }

    // ❌ Delete a task
    public void deleteTask(Long taskId, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));

        if (!task.getUser().equals(user)) {
            throw new SecurityException("You can only delete your own tasks!");
        }

        taskRepository.delete(task);
        activityService.log("Deleted Task: " + task.getTitle(), ActivityType.TASK, task.getUser());
    }

    // ✅ Complete a task with completion timestamp
    public TaskResponseDTO completeTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));

        if (!task.isCompleted()) {
            task.setCompleted(true);
            task.setCompletionDate(java.time.LocalDate.now());
            task.setCompletedDateTime(java.time.LocalDateTime.now());

            Task savedTask = taskRepository.save(task);

            activityService.log("Completed Task: " + savedTask.getTitle(), ActivityType.TASK, savedTask.getUser());
            streakTrackerService.updateStreak(savedTask.getUser());

            // ✅ Check for new achievements
            achievementService.checkForNewAchievements(savedTask.getUser());

            return TaskMapper.toResponse(savedTask);
        }

        return TaskMapper.toResponse(task);
    }

    // 🔹 Count of completed tasks for a user
    public int getCompletedTaskCount(User user) {
        return taskRepository.findByUserAndCompletedTrue(user).size();
    }

	public Object getAllTasks(Object any) {
		// TODO Auto-generated method stub
		return null;
	}
}
