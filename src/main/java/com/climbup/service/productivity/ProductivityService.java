package com.climbup.service.productivity;

import org.springframework.stereotype.Service;

import com.climbup.model.User;
import com.climbup.repository.TaskRepository;

@Service
public class ProductivityService {

    private final TaskRepository taskRepository;
    private final StreakTrackerService streakTrackerService;

    public ProductivityService(TaskRepository taskRepository,
    		StreakTrackerService streakTrackerService) {
        this.taskRepository = taskRepository;
        this.streakTrackerService = streakTrackerService;
    }

    public int calculate(User user) {

        int completedTasks =
                (int) taskRepository.countByUserAndCompleted(user, true);

        int score = 0;

        score += Math.min(completedTasks * 2, 40);
        int currentStreak = streakTrackerService.getCurrentStreak(user);
        score += Math.min(currentStreak * 3, 30);

        score += Math.min(user.getLevel() * 5, 30);

        return Math.min(score, 100);
    }
}
