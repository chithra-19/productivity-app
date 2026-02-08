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
    private final XPService xpService;

    public DashboardService(TaskService taskService,
            AchievementService achievementService,
            StreakTrackerService streakTrackerService,
            XPService xpService) {

this.taskService = taskService;
this.achievementService = achievementService;
this.streakTrackerService = streakTrackerService;
this.xpService = xpService;
}


    /**
     * 🔹 Fetch all tasks for user
     */
    public List<TaskResponseDTO> getAllTasks(User user) {
        return taskService.getTasksForUser(user);
    }

    public List<TaskResponseDTO> getTodayTasks(User user) {
        return taskService.getTodayTasks(user);
    }

    public Map<String, Integer> getTaskStats(User user) {
        List<TaskResponseDTO> tasks = taskService.getTasksForUser(user);

        int total = tasks.size();
        int completed = (int) tasks.stream().filter(TaskResponseDTO::isCompleted).count();
        int pending = total - completed;

        return Map.of(
                "total", total,
                "completed", completed,
                "pending", pending
        );
    }

    /**
     * 🔹 Current streak
     */
    public int getCurrentStreak(User user) {
        return streakTrackerService.getCurrentStreak(user);
    }

    /**
     * 🔹 Achievements
     */
    public Object getUserAchievements(User user) {
        achievementService.initializeAchievements(user);
        return achievementService.getUserAchievements(user);
    }
    
    public DashboardSummaryDTO getDashboardSummary(User user) {

        DashboardSummaryDTO dto = new DashboardSummaryDTO();

        dto.setLevel(user.getLevel());
        dto.setXp(user.getXp());
        dto.setCurrentStreak(streakTrackerService.getCurrentStreak(user));
        dto.setBestStreak(streakTrackerService.getCurrentStreak(user));


        dto.setXpForNextLevel(xpService.xpForNextLevel(user));
        dto.setXpProgress(xpService.xpProgressInCurrentLevel(user));

        dto.setTaskStats(getTaskStats(user));

        return dto;
    }

}
