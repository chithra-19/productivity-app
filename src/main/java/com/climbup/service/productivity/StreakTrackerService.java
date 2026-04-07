package com.climbup.service.productivity;

import com.climbup.model.ActivityType;
import com.climbup.model.StreakTracker;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.StreakTrackerRepository;
import com.climbup.repository.TaskRepository;
import com.climbup.repository.UserRepository;
import com.climbup.service.task.ActivityService;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

@Service
public class StreakTrackerService {

	 private static final String DEFAULT_CATEGORY = "GLOBAL";
	
    private final StreakTrackerRepository streakTrackerRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ActivityService activityService;

    public StreakTrackerService(StreakTrackerRepository streakTrackerRepository,
    							TaskRepository taskRepository,
    							UserRepository userRepository,
    							ActivityService activityService) {
    		this.streakTrackerRepository = streakTrackerRepository;
    		this.taskRepository = taskRepository;
    		this.userRepository = userRepository;
    		this.activityService = activityService;
}
 
    private StreakTracker createTracker(User user, String category) {
        StreakTracker tracker = new StreakTracker();
        tracker.setUser(user);
        tracker.setCategory(category);
        return tracker;
    }

    // 🔹 Get current streak
    public int getCurrentStreak(User user, String category) {
        return streakTrackerRepository.findByUserAndCategory(user, category)
                .map(StreakTracker::getCurrentStreak)
                .orElse(0);
    }

    // 🔹 Get all streaks for a user
    public List<StreakTracker> getAllStreaksForUser(Long userId) {
        return streakTrackerRepository.findAllByUserId(userId);
    }
    
    public int calculateXP(User user) {
        List<Task> completedTasks = taskRepository.findByUserAndCompletedTrue(user);

        int xp = 0;
        for (Task task : completedTasks) {
            xp += 5; // base XP per task
            if (task.getFocusHours() != null) {
                xp += task.getFocusHours().intValue(); // bonus XP for focus
            }
        }

        return xp;
    }


    // 🔹 Heatmap data (completed tasks per day)
    public Map<LocalDate, Integer> getHeatmapData(User user, String category) {

        List<Task> tasks =
                taskRepository.findByUserIdAndCategory(user.getId(), category);

        return tasks.stream()
                .filter(Task::isCompleted)
                .filter(t -> t.getCompletedDateTime() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCompletedDateTime()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        Collectors.summingInt(t -> 1)
                ));
    }

    public void refreshUserStreak(User user) {

        int oldStreak = user.getCurrentStreak(); // 👈 track previous

        int current = getCurrentStreak(user);
        int best = getBestStreak(user);

        user.setCurrentStreak(current);
        user.setBestStreak(best);

        // 🔥 LOG ONLY IF STREAK CHANGED
        if (current != oldStreak) {
            activityService.log(
                "Streak: " + current + " days 🔥",
                ActivityType.STREAK_UPDATED,
                user
            );
        }
    }
    public int getBestStreak(User user) {

        List<LocalDate> dates = taskRepository.findCompletedDates(user)
                .stream()
                .sorted()
                .toList();

        if (dates.isEmpty()) return 0;

        int best = 1, current = 1;

        for (int i = 1; i < dates.size(); i++) {
            if (dates.get(i).equals(dates.get(i - 1).plusDays(1))) {
                current++;
            } else {
                best = Math.max(best, current);
                current = 1;
            }
        }

        return Math.max(best, current);
    }
    public int getCurrentStreak(User user) {

        List<LocalDate> dates = taskRepository.findCompletedDates(user)
                .stream()
                .sorted(Comparator.reverseOrder())
                .toList();

        if (dates.isEmpty()) return 0;

        int streak = 0;
        LocalDate today = LocalDate.now();

        // 🔥 KEY FIX
        boolean hasToday = dates.contains(today);

        LocalDate expectedDate = hasToday ? today : today.minusDays(1);

        for (LocalDate date : dates) {
            if (date.equals(expectedDate.minusDays(streak))) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }
    
    public List<String> getBadgeLabels(StreakTracker tracker) {
        int longest = tracker.getLongestStreak();
        List<String> badges = new ArrayList<>();

        if (longest >= 50) badges.add("50-Day Consistency Badge 🟢");
        if (longest >= 100) badges.add("100-Day Consistency Badge 🔵");
        if (longest >= 365) badges.add("365-Day Consistency Badge 🏆");

        return badges;
    }
    
    
    public Map<LocalDate, Integer> getHeatmapData(List<Task> tasks) {
        return tasks.stream()
                .filter(Task::isCompleted)
                .filter(t -> t.getCompletedDateTime() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCompletedDateTime()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        Collectors.summingInt(t -> 1)
                ));
    }

    public StreakTracker getStreakByUserAndCategory(Long userId, String category) {
        return streakTrackerRepository.findByUserIdAndCategory(userId, category)
                .orElse(null); // or throw exception if preferred
    }
    
  

    private void resetWeeklyFreezeIfNeeded(User user, LocalDate today) {
        if (user.getLastFreezeResetDate() == null ||
            ChronoUnit.DAYS.between(user.getLastFreezeResetDate(), today) >= 7) {

            user.setAvailableFreezes(1);
            user.setLastFreezeResetDate(today);
        }
    }

   

   
}
