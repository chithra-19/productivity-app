package com.climbup.service.productivity;

import com.climbup.model.User;
import com.climbup.model.StreakTracker;
import com.climbup.model.Activity.ActivityType;
import com.climbup.repository.StreakTrackerRepository;
import com.climbup.service.task.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class StreakTrackerService {

    private final StreakTrackerRepository repository;
    private final ActivityService activityService;

    @Autowired
    public StreakTrackerService(StreakTrackerRepository repository, ActivityService activityService) {
        this.repository = repository;
        this.activityService = activityService;
    }

    /**
     * Updates the user's streak for a given category.
     * If the streak does not exist, it will create a new one.
     */
    public StreakTracker updateStreak(User user, String category) {
        LocalDate today = LocalDate.now();

        // Fetch streak for the user & category, or create a new one
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

        // If already updated today
        if (tracker.hasUpdatedToday(today)) {
            log(user, category, "Streak already updated today 📅");
            return tracker;
        }

        // Calculate days between last update and today
        long daysBetween = tracker.getLastActiveDate() != null
                ? ChronoUnit.DAYS.between(tracker.getLastActiveDate(), today)
                : 0;

        if (daysBetween == 1) { // streak continues
            tracker.setCurrentStreak(tracker.getCurrentStreak() + 1);
            tracker.setLastActiveDate(today);
            if (tracker.getCurrentStreak() > tracker.getLongestStreak()) {
                tracker.setLongestStreak(tracker.getCurrentStreak());
                log(user, category, "New longest streak: " + tracker.getCurrentStreak() + " days 🏆");
            } else {
                log(user, category, "Streak continued: " + tracker.getCurrentStreak() + " days ✅");
            }
        } else if (daysBetween > 1 || tracker.getLastActiveDate() == null) { // streak reset
            tracker.setCurrentStreak(1);
            tracker.setLastActiveDate(today);
            log(user, category, "Streak reset after " + daysBetween + " days 😢");
        }

        return repository.save(tracker);
    }

    /**
     * Generic update for default category (so TaskService doesn’t break).
     */
    public void updateStreak(User user) {
        updateStreak(user, "GENERAL"); // ✅ default category
    }

    /**
     * Get a user's streak by category
     */
    public StreakTracker getStreakByUserAndCategory(Long userId, String category) {
        return repository.findByUserIdAndCategory(userId, category)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Streak not found for user and category: " + category));
    }

    /**
     * Get all streaks for a user
     */
    public List<StreakTracker> getAllStreaksForUser(Long userId) {
        return repository.findAllByUserId(userId);
    }

    /**
     * Log streak activity
     */
    private void log(User user, String category, String message) {
        activityService.log("[" + category + "] " + message, ActivityType.STREAK, user);
    }
}
