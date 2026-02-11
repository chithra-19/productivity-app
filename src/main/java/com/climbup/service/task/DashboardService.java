package com.climbup.service.task;

import com.climbup.dto.response.DashboardSummaryDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.model.User;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.task.TaskService;
import org.springframework.stereotype.Service;
import com.climbup.service.productivity.XPService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@Service
public class DashboardService {

    private final TaskService taskService;
    private final AchievementService achievementService;
    private final StreakTrackerService streakTrackerService;

    public DashboardService(TaskService taskService,
                            AchievementService achievementService,
                            StreakTrackerService streakTrackerService) {

        this.taskService = taskService;
        this.achievementService = achievementService;
        this.streakTrackerService = streakTrackerService;
    }

    public Map<String, Integer> getTaskStats(User user) {
        List<TaskResponseDTO> tasks = taskService.getTasksForUser(user);

        int total = tasks.size();
        int completed = (int) tasks.stream()
                .filter(TaskResponseDTO::isCompleted)
                .count();

        return Map.of(
                "total", total,
                "completed", completed,
                "pending", total - completed
        );
    }

    public DashboardSummaryDTO getDashboardSummary(User user) {

        DashboardSummaryDTO dto = new DashboardSummaryDTO();

        int score = user.getProductivityScore();
        dto.setProductivityScore(score);

        // 🔥 Human-readable meaning
        if (score < 40) {
            dto.setProductivityLabel("LOW");
        } else if (score < 70) {
            dto.setProductivityLabel("MEDIUM");
        } else {
            dto.setProductivityLabel("HIGH");
        }

        dto.setCurrentStreak(
        	    streakTrackerService.getCurrentStreak(user)
        	);

        	dto.setBestStreak(
        	    streakTrackerService.getBestStreak(user.getId())
        	);


        dto.setTaskStats(getTaskStats(user));

        return dto;
    }
}
