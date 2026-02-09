package com.climbup.service.productivity;

import org.springframework.stereotype.Service;

import com.climbup.model.User;
import com.climbup.repository.TaskRepository;

@Service
public class ProductivityService {

    private final TaskRepository taskRepository;

    public ProductivityService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public int calculate(User user) {

        int completedTasks =
                (int) taskRepository.countByUserAndCompleted(user, true);

        int score = 0;

        score += Math.min(completedTasks * 2, 40);
        score += Math.min(user.getCurrentStreak() * 3, 30);
        score += Math.min(user.getLevel() * 5, 30);

        return Math.min(score, 100);
    }
}
