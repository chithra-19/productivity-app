package com.climbup.service.productivity;

import com.climbup.model.StreakTracker;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.StreakTrackerRepository;
import com.climbup.repository.TaskRepository;
import com.climbup.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StreakTrackerService {

	 private static final String DEFAULT_CATEGORY = "GLOBAL";
	
    private final StreakTrackerRepository streakTrackerRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public StreakTrackerService(StreakTrackerRepository streakTrackerRepository,
                                TaskRepository taskRepository,
                                UserRepository userRepository) {
        this.streakTrackerRepository = streakTrackerRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }
    
   

    // 🔹 Evaluate streak for TODAY (call after task completion)
    @Transactional
    public void evaluateToday(User user) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        // Count completed tasks today
        long completedTasks = taskRepository
        	    .countByUserAndCompletedTrueAndCompletedDateTimeBetween(user, start, end);

        boolean qualifiedToday = completedTasks > 0;

        StreakTracker tracker = streakTrackerRepository
            .findByUserAndCategory(user, DEFAULT_CATEGORY)
            .orElseGet(() -> createTracker(user, DEFAULT_CATEGORY));

        tracker.updateForDay(today, qualifiedToday);

        // Optional: weekly freeze logic
        resetWeeklyFreezeIfNeeded(user, today);

        streakTrackerRepository.save(tracker);
        userRepository.save(user);
    }
    
    public void updateStreakAfterThreshold(User user, long todayCompleted) {

        LocalDate today = LocalDate.now();
        var profile = user.getProfile();

        LocalDate lastDate = profile.getLastActiveDate();

        // ❌ already updated today
        if (lastDate != null && lastDate.isEqual(today)) {
            return;
        }

        if (todayCompleted >= 3) {

            if (lastDate != null && lastDate.plusDays(1).isEqual(today)) {
                // ✅ normal streak continuation
                profile.setStreak(profile.getStreak() + 1);

            } else if (lastDate != null && lastDate.plusDays(2).isEqual(today)
                       && profile.getStreakFreezeCount() > 0) {

                // 🧊 USE FREEZE
                profile.setStreak(profile.getStreak() + 1);
                profile.setStreakFreezeCount(profile.getStreakFreezeCount() - 1);

            } else {
                // ❌ reset streak
                profile.setStreak(1);
            }

            profile.setLastActiveDate(today);
        }
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
                        t -> t.getCompletedDateTime().toLocalDate(),
                        Collectors.summingInt(t -> 1)
                ));
    }

    // 🔹 Best (longest) streak
    public int getBestStreak(Long userId) {
        return streakTrackerRepository.getUserBestStreak(userId).orElse(0);
    }
    
    public StreakTracker updateStreak(User user, String category, boolean qualifiedToday) {
        LocalDate today = LocalDate.now();

        StreakTracker tracker = streakTrackerRepository
                .findByUserAndCategory(user, category)
                .orElseGet(() -> {
                    StreakTracker st = new StreakTracker();
                    st.setUser(user);
                    st.setCategory(category);
                    return st;
                });

        tracker.updateForDay(today, qualifiedToday);
        streakTrackerRepository.save(tracker);
        return tracker;  // <-- return the updated entity
    }
    
    public int getBestStreak(User user) {
        List<Object[]> streakDays = taskRepository.findStreakEligibleDates(user);
        Set<LocalDate> eligibleDates = streakDays.stream()
            .map(row -> (LocalDate) row[0])
            .collect(Collectors.toSet());

        int bestStreak = 0;
        int currentStreak = 0;
        LocalDate today = LocalDate.now();

        for (LocalDate date = today; date.isAfter(today.minusDays(365)); date = date.minusDays(1)) {
            if (eligibleDates.contains(date)) {
                currentStreak++;
                bestStreak = Math.max(bestStreak, currentStreak);
            } else {
                currentStreak = 0;
            }
        }

        return bestStreak;
    }

    // Overload for default qualifiedToday = true
    public StreakTracker updateStreak(User user, String category) {
        return updateStreak(user, category, true);
    }

    public void updateStreak(User user) {
        updateStreak(user, "TASK", true);
    }


    
    public int calculateCurrentStreak(Long userId) {
        return streakTrackerRepository.findByUserIdAndCategory(userId, "TASK")
                .map(StreakTracker::getCurrentStreak)
                .orElse(0);
    }
    
    public int getCurrentStreak(User user) {
        List<Object[]> streakDays = taskRepository.findStreakEligibleDates(user);
        int streak = 0;
        LocalDate expectedDate = LocalDate.now();

        for (Object[] row : streakDays) {
            LocalDate date = (LocalDate) row[0];
            if (date.equals(expectedDate)) {
                streak++;
                expectedDate = expectedDate.minusDays(1);
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
                        t -> t.getCompletedDateTime().toLocalDate(),
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
