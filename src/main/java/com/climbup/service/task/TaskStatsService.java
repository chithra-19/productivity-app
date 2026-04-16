package com.climbup.service.task;

import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskStatsService {

    private final TaskRepository taskRepository;

    public TaskStatsService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // =========================================================
    // COMPLETED TASK COUNT
    // =========================================================
    public long getCompletedTaskCount(User user) {
        return taskRepository.countByUserAndCompleted(user, true);
    }

    // =========================================================
    // PENDING TASK COUNT
    // =========================================================
    public long getPendingTaskCount(User user) {
        return taskRepository.countByUserAndCompletedFalse(user);
    }

    // =========================================================
    // TASK CREATED DAILY STATS
    // =========================================================
    public Map<LocalDate, Long> getTaskStats(User user) {

        return taskRepository.findByUser(user)
                .stream()
                .filter(task -> task.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        task -> task.getCreatedAt()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        Collectors.counting()
                ));
    }

   
    // =========================================================
    // COMPLETED TASKS BY DATE (HEATMAP)
    // =========================================================
    public Map<LocalDate, Integer> getCompletedTaskHeatmap(User user) {

        return taskRepository.findByUserAndCompletedTrue(user)
                .stream()
                .filter(task -> task.getCompletedDateTime() != null)
                .collect(Collectors.groupingBy(
                        task -> task.getCompletedDateTime()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        Collectors.summingInt(t -> 1)
                ));
    }

    // =========================================================
    // FOCUS HOURS BY DATE
    // =========================================================
    public Map<LocalDate, Double> getFocusHoursByDate(User user) {

        return taskRepository.findByUserAndCompletedTrue(user)
                .stream()
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

    // =========================================================
    // TODAY TASK COUNT
    // =========================================================
    public long getTodayTaskCount(User user) {

        LocalDate today = LocalDate.now();

        return taskRepository.findByUser(user)
                .stream()
                .filter(task -> today.equals(task.getTaskDate()))
                .count();
    }

    // =========================================================
    // OVERALL TASK DISTRIBUTION
    // =========================================================
    public Map<String, Long> getTaskDistribution(User user) {

        return taskRepository.findByUser(user)
                .stream()
                .collect(Collectors.groupingBy(
                        task -> task.getPriority().name(),
                        Collectors.counting()
                ));
    }
}