package com.climbup.service.productivity;

import com.climbup.model.User;
import com.climbup.model.StreakTracker;
import com.climbup.model.Task;
import com.climbup.model.Activity.ActivityType;
import com.climbup.repository.StreakTrackerRepository;
import com.climbup.repository.TaskRepository;
import com.climbup.service.task.ActivityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StreakTrackerService {

    private final StreakTrackerRepository repository;
    private final ActivityService activityService;
    private final TaskRepository taskRepository;

    @Autowired
    public StreakTrackerService(StreakTrackerRepository repository,
                                ActivityService activityService,
                                TaskRepository taskRepository) {
        this.repository = repository;
        this.activityService = activityService;
        this.taskRepository = taskRepository;
    }

    // 🔄 Update streak for a given category
    public StreakTracker updateStreak(User user, String category) {
        LocalDate today = LocalDate.now();

        StreakTracker tracker = repository.findByUserIdAndCategory(user.getId(), category)
                .orElseGet(() -> {
                    StreakTracker newTracker = new StreakTracker();
                    newTracker.setUser(user);
                    newTracker.setCategory(category);
                    newTracker.setCurrentStreak(1);
                    newTracker.setLongestStreak(1);
                    newTracker.setLastActiveDate(today);
                    log(user, category, "Started first streak 🔥");
                    return newTracker;
                });

        if (tracker.hasUpdatedToday(today)) {
            log(user, category, "Streak already updated today 📅");
            return tracker;
        }

        long daysBetween = tracker.getLastActiveDate() != null
                ? ChronoUnit.DAYS.between(tracker.getLastActiveDate(), today)
                : 0;

        if (daysBetween == 1) {
            tracker.setCurrentStreak(tracker.getCurrentStreak() + 1);
            tracker.setLastActiveDate(today);

            if (tracker.getCurrentStreak() > tracker.getLongestStreak()) {
                tracker.setLongestStreak(tracker.getCurrentStreak());
                log(user, category, "New longest streak: " + tracker.getCurrentStreak() + " days 🏆");
            } else {
                log(user, category, "Streak continued: " + tracker.getCurrentStreak() + " days ✅");
            }

        } else if (daysBetween > 1) {
            tracker.setCurrentStreak(1);
            tracker.setLastActiveDate(today);
            log(user, category, "Streak reset after " + daysBetween + " days 😢");
        }

        return repository.save(tracker);
    }

    public void updateStreak(User user) {
        updateStreak(user, "GENERAL");
    }

    // 📊 Get current streak based on completed tasks
    public int getCurrentStreak(User user) {
        List<Task> completedTasks = taskRepository
                .findByUserAndCompletedTrueOrderByCompletedDateTimeDesc(user);

        if (completedTasks.isEmpty()) return 0;

        int streak = 0;
        LocalDate current = LocalDate.now();

        for (Task task : completedTasks) {
            LocalDate completedDate = task.getCompletionDate();
            if (completedDate == null) continue;

            if (completedDate.equals(current)) {
                streak++;
                current = current.minusDays(1);
            } else if (completedDate.isBefore(current)) {
                break;
            }
        }
        return streak;
    }

    public StreakTracker getStreakByUserAndCategory(Long userId, String category) {
        return repository.findByUserIdAndCategory(userId, category)
                .orElseThrow(() -> new IllegalArgumentException("Streak not found for user and category: " + category));
    }

    public List<StreakTracker> getAllStreaksForUser(Long userId) {
        return repository.findAllByUserId(userId);
    }

    private void log(User user, String category, String message) {
        activityService.log("[" + category + "] " + message, ActivityType.STREAK, user);
    }

    public List<String> getBadgeLabels(StreakTracker tracker) {
        int longest = tracker.getLongestStreak();
        List<String> badges = new ArrayList<>();

        if (longest >= 50) badges.add("50-Day Consistency Badge 🟢");
        if (longest >= 100) badges.add("100-Day Consistency Badge 🔵");
        if (longest >= 365) badges.add("365-Day Consistency Badge 🏆");

        return badges;
    }

    public Map<String, Integer> getHeatmapData(List<Task> tasks) {
        return tasks.stream()
                .filter(Task::isCompleted)
                .filter(task -> task.getCompletionDate() != null)
                .collect(Collectors.groupingBy(
                        task -> task.getCompletionDate().toString(),
                        Collectors.summingInt(t -> 1)
                ));
    }

    // 🏅 Best streak calculated from DB
    public int getBestStreak(Long userId) {
        return repository.getUserBestStreak(userId).orElse(0);
    }
    
    public int calculateCurrentStreak(Long userId) {
        List<StreakTracker> streaks = repository.findByUserIdOrderByLastActiveDateAsc(userId);

        if (streaks.isEmpty()) return 0;

        int streak = 1;
        StreakTracker last = streaks.get(0);

        for (int i = 1; i < streaks.size(); i++) {
            StreakTracker current = streaks.get(i);

            long diff = ChronoUnit.DAYS.between(last.getLastActiveDate(), current.getLastActiveDate());

            if (diff == 1) {
                streak++;
            } else if (diff > 1) {
                streak = 1;
            }

            last = current;
        }

        return streak;
    }


}
