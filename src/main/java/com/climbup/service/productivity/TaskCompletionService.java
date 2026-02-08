package com.climbup.service.productivity;

import com.climbup.model.Task;
import com.climbup.service.productivity.XPService;
import com.climbup.model.User;
import com.climbup.service.task.TaskService;
import com.climbup.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskCompletionService {

    private final XPService xpService;
    private final StreakTrackerService streakTrackerService;
    private final AchievementService achievementService;
    private final UserService userService;
    private final TaskService taskService;

    public TaskCompletionService(XPService xpService,
                                 StreakTrackerService streakTrackerService,
                                 AchievementService achievementService,
                                 UserService userService,
                                 TaskService taskService) {
        this.xpService = xpService;
        this.streakTrackerService = streakTrackerService;
        this.achievementService = achievementService;
        this.userService = userService;
        this.taskService = taskService;
    }

    @Transactional
    public void completeTask(Task task, User user) {

        if (task.isCompleted()) return;

        // 1️⃣ mark task complete
        task.setCompleted(true);
        taskService.save(task);

        // 2️⃣ XP gain
        xpService.addXp(user, 10);

        // 3️⃣ streak update
        streakTrackerService.updateStreak(user);

        // 4️⃣ achievement check
        achievementService.evaluateAchievements(user);

        userService.save(user);
    }
}
