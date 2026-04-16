package com.climbup.service.task;

import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.mapper.TaskMapper;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskQueryService {

    private final TaskRepository taskRepository;

    public TaskQueryService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // =========================================================
    // ALL TASKS
    // =========================================================
    public List<TaskResponseDTO> getTasksForUser(User user) {

        return taskRepository.findByUser(user)
                .stream()
                .map(task -> TaskMapper.toResponse(task, getPriorityPoints(task)))
                .collect(Collectors.toList());
    }

    // =========================================================
    // TASK BY ID
    // =========================================================
    public TaskResponseDTO getTaskById(Long taskId, User user) {

        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        return TaskMapper.toResponse(task, getPriorityPoints(task));
    }

    // =========================================================
    // TODAY TASKS
    // =========================================================
    public List<TaskResponseDTO> getTodayTasks(User user) {

        LocalDate today = LocalDate.now();

        return taskRepository.findByUser(user)
                .stream()
                .filter(task -> today.equals(task.getTaskDate()))
                .map(task -> TaskMapper.toResponse(task, getPriorityPoints(task)))
                .collect(Collectors.toList());
    }

    // =========================================================
    // BACKLOG TASKS
    // =========================================================
    public List<TaskResponseDTO> getBacklogTasks(User user) {

        return taskRepository.findByUser(user)
                .stream()
                .filter(task -> task.getTaskDate() == null)
                .map(task -> TaskMapper.toResponse(task, getPriorityPoints(task)))
                .collect(Collectors.toList());
    }

    // =========================================================
    // PENDING TASKS
    // =========================================================
    public List<Task> getPendingTasks(User user) {
        return taskRepository.findByUserAndCompletedFalse(user);
    }

    // =========================================================
    // TASKS BY DUE DATE
    // =========================================================
    public List<Task> getTasksDueOn(User user, LocalDate date) {
        return taskRepository.findByUserAndDueDate(user, date);
    }

    // =========================================================
    // PAGINATED TASKS
    // =========================================================
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

        return taskRepository.findByUser(user, pageable)
                .map(task -> TaskMapper.toResponse(task, getPriorityPoints(task)));
    }

    // =========================================================
    // TOP 5 TODAY TASKS
    // =========================================================
    public List<TaskResponseDTO> getTop5TodayTasks(User user) {

        LocalDate today = LocalDate.now();

        return taskRepository.findByUserAndTaskDate(user, today)
                .stream()
                .sorted(Comparator.comparing(Task::getPriority).reversed())
                .limit(5)
                .map(task -> TaskMapper.toResponse(task, getPriorityPoints(task)))
                .collect(Collectors.toList());
    }

    // =========================================================
    // PRIORITY POINTS (UI helper only)
    // =========================================================
    private int getPriorityPoints(Task task) {

        return switch (task.getPriority()) {
            case LOW -> 1;
            case MEDIUM -> 3;
            case HIGH -> 5;
        };
    }
    public List<TaskResponseDTO> getTasksForUserByDate(User user, LocalDate date) {

        return taskRepository.findByUserAndTaskDate(user, date)
                .stream()
                .map(task -> TaskMapper.toResponse(task, getPriorityPoints(task)))
                .toList();
    }
}