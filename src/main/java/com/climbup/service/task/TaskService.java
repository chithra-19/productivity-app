package com.climbup.service.task;

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.request.TaskUpdateDTO;

import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.mapper.TaskMapper;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.model.ActivityType;
import com.climbup.repository.TaskRepository;
import com.climbup.repository.UserRepository;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.productivity.ProductivityService;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.productivity.XPService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ActivityService activityService;
    private final StreakTrackerService streakTrackerService;
    private final AchievementService achievementService;
    private final UserRepository userRepository;
    private final XPService xpService;
    private final ProductivityService productivityService;
    

    @Autowired
    public TaskService(TaskRepository taskRepository,
                       ActivityService activityService,
                       StreakTrackerService streakTrackerService,
                       @Lazy AchievementService achievementService,
                       UserRepository userRepository,
                       XPService xpService,
                       ProductivityService productivityService) {
        this.taskRepository = taskRepository;
        this.activityService = activityService;
        this.streakTrackerService = streakTrackerService;
        this.achievementService = achievementService;
        this.userRepository = userRepository;
        this.xpService = xpService;
        this.productivityService = productivityService;
     

    }
    
    private TaskResponseDTO convertToDTO(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();

        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCategory(task.getCategory());
        dto.setPriority(task.getPriority().name());
        dto.setCompleted(task.isCompleted());
        dto.setDueDate(task.getDueDate());
        dto.setCompletedDateTime(task.getCompletedDateTime());

        return dto;
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
        task.setTaskDate(LocalDate.now());

        Task savedTask = taskRepository.save(task);

        // 🔥 ADD THIS
        activityService.log(
            "Created task: " + savedTask.getTitle(),
            ActivityType.TASK_CREATED,
            user
        );

        return TaskMapper.toResponse(savedTask, getPriorityPoints(savedTask.getPriority()));
    }
    // 📋 Get all tasks for a user
    public List<TaskResponseDTO> getTasksForUser(User user) {
        return taskRepository.findByUser(user)
                .stream()
                .map(task -> TaskMapper.toResponse(task, getPriorityPoints(task.getPriority())))
                .collect(Collectors.toList());
    }

 // ✏️ Update Task
    @Transactional
    public TaskResponseDTO updateTask(Long taskId, TaskUpdateDTO dto, User user) {

        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() ->
                        new IllegalArgumentException("Task not found with ID: " + taskId));

        // 📝 Update editable fields
        Optional.ofNullable(dto.getTitle()).ifPresent(task::setTitle);
        Optional.ofNullable(dto.getDescription()).ifPresent(task::setDescription);
        Optional.ofNullable(dto.getDueDate()).ifPresent(task::setDueDate);
        Optional.ofNullable(dto.getPriority()).ifPresent(task::setPriority);
        Optional.ofNullable(dto.getCategory()).ifPresent(task::setCategory);


        // ↩️ Mark as incomplete (explicit false)
        if (Boolean.FALSE.equals(dto.getCompleted())) {
            task.setCompleted(false);
            task.setCompletedDateTime(null);
        }

        Task updatedTask = taskRepository.save(task);
      
        return TaskMapper.toResponse(updatedTask, getPriorityPoints(updatedTask.getPriority()));
    }
    // ❌ Delete Task
    public void deleteTask(Long taskId, User user) {
        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));


        taskRepository.delete(task);
       
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
                        task -> task.getCompletedDateTime()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        Collectors.summingDouble(
                                task -> task.getFocusHours() != null
                                        ? task.getFocusHours()
                                        : 0.0
                        )
                ));
    }  
    // 🔹 Today’s tasks
    public List<TaskResponseDTO> getTodayTasks(User user) {
        LocalDate today = LocalDate.now();

        return taskRepository.findByUser(user)
                .stream()
                .filter(task ->
                        task.getTaskDate() != null &&
                        task.getTaskDate().equals(today))
                .map(task -> TaskMapper.toResponse(task, getPriorityPoints(task.getPriority())))
                .collect(Collectors.toList());
    }


    public Map<LocalDate, Long> getTaskStats(User user) {
        return taskRepository.findByUser(user).stream()
                .filter(task -> task.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        task -> task.getCreatedAt()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        Collectors.counting()
                ));
    }
    public List<TaskResponseDTO> getBacklogTasks(User user) {
        return taskRepository.findByUser(user)
                .stream()
                .filter(task -> task.getTaskDate() == null)
                .map(task -> TaskMapper.toResponse(task, getPriorityPoints(task.getPriority())))
                .collect(Collectors.toList());
    }
    public int countCompletedTasks(Long userId) {
        User user = new User();
        user.setId(userId);
        return (int) taskRepository.countByUserAndCompleted(user, true);
    }

    public List<Task> getPendingTasks(User user) {
        return taskRepository.findByUserAndCompletedFalse(user);
    }

    public List<Task> getTasksDueOn(User user, LocalDate date) {
        return taskRepository.findByUserAndDueDate(user, date);
    }

    public boolean markTaskAsCompleted(Long taskId, User user) {
        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.isCompleted()) return false;

        completeTask(taskId, user);
    
        return true;
    }
  public Map<LocalDate, Long> getTaskCountsByDate(User user) {
        List<Task> tasks = taskRepository.findByUser(user);
        return tasks.stream()
                    .collect(Collectors.groupingBy(Task::getDueDate, Collectors.counting()));
    }
    
    public Page<TaskResponseDTO> getTasksForUserPaginated(
            User user,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return taskRepository
                .findByUser(user, pageable)
                .map(task -> TaskMapper.toResponse(task, getPriorityPoints(task.getPriority())));
    }

    @Transactional
    public void completeTask(Long taskId, User user) {

        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.isCompleted()) return;

        // ✅ mark complete
        task.setCompleted(true);
        task.setCompletedDateTime(Instant.now());

        // 🔥 critical fix
        if (task.getTaskDate() == null) {
            task.setTaskDate(LocalDate.now());
        }

        taskRepository.save(task);

     // 🔥 ADD THIS LINE
     activityService.logTaskCompleted(task, user);
      
        
        // 🔥 FIXED LOGIC
        LocalDate today = LocalDate.now();

        boolean qualifiedToday = taskRepository
                .findByUserAndTaskDate(user, today)
                .stream()
                .anyMatch(Task::isCompleted);

        streakTrackerService.refreshUserStreak(user);
        userRepository.save(user);

        xpService.handleTaskCompletion(user, getPriorityPoints(task.getPriority()));
        achievementService.evaluateAchievements(user);
     

        int score = productivityService.calculate(user);
        user.setProductivityScore(score);

        userRepository.save(user);
    }


    public TaskResponseDTO getTaskById(Long taskId, User user) {
        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        return TaskMapper.toResponse(task, getPriorityPoints(task.getPriority()));
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public Task markDone(Long id, User user) {

        completeTask(id, user); // 🔥 reuse full logic

        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }
    
    public List<TaskResponseDTO> getTasksForUserByDate(User user, LocalDate date) {
        return taskRepository.findByUserAndTaskDate(user, date)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    public List<TaskResponseDTO> getTop5TodayTasks(User user) {

        return taskRepository.findByUserAndTaskDate(user, LocalDate.now())
                .stream()
                .sorted(Comparator.comparing(Task::getPriority).reversed())
                .limit(5)
                .map(task -> TaskMapper.toResponse(task, getPriorityPoints(task.getPriority())))
                .toList();
    }
   
    public int getPriorityPoints(Task.Priority priority) {
        return switch (priority) {
            case LOW -> 1;
            case MEDIUM -> 3;
            case HIGH -> 5;
        };
    }

}
