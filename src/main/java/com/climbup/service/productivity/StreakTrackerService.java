package com.climbup.service.productivity;

import com.climbup.model.StreakTracker;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.StreakTrackerRepository;
import com.climbup.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StreakTrackerService {

    private final StreakTrackerRepository streakTrackerRepository;
    private final TaskRepository taskRepository;

    public StreakTrackerService(StreakTrackerRepository streakTrackerRepository,
                                TaskRepository taskRepository) {
        this.streakTrackerRepository = streakTrackerRepository;
        this.taskRepository = taskRepository;
    }

    // 🔹 Evaluate streak for TODAY (call after task completion)
    @Transactional
    public void evaluateToday(User user, String category) {

        LocalDate today = LocalDate.now();

        long totalTasks =
                taskRepository.countByUserAndCategoryAndDueDate(user, category, today);

        // ❌ No tasks today → streak unchanged
        if (totalTasks == 0) return;

        long completedTasks =
                taskRepository.countByUserAndCategoryAndDueDateAndCompletedTrue(
                        user, category, today
                );

        boolean qualifiedToday = (totalTasks == completedTasks);

        StreakTracker tracker = streakTrackerRepository
                .findByUserAndCategory(user, category)
                .orElseGet(() -> createTracker(user, category));

        // 🧠 ENTITY handles streak rules
        tracker.updateForDay(today, qualifiedToday);

        streakTrackerRepository.save(tracker);
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
        return getCurrentStreak(user, "TASK");
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
    

    public void handleTaskCompletion(User user) {
        LocalDate today = LocalDate.now();

        resetWeeklyFreezeIfNeeded(user, today);

        if (user.getLastActiveDate() == null) {
            user.setCurrentStreak(1);
        } else {
            long gap = ChronoUnit.DAYS.between(user.getLastActiveDate(), today);

            if (gap == 0) {
                return; // same day, ignore
            }

            if (gap == 1) {
                user.setCurrentStreak(user.getCurrentStreak() + 1);
            } else {
                if (user.getAvailableFreezes() > 0) {
                    user.setAvailableFreezes(0); // consume freeze
                } else {
                    user.setCurrentStreak(1); // reset streak
                }
            }
        }

        user.setLastActiveDate(today);
    }

    private void resetWeeklyFreezeIfNeeded(User user, LocalDate today) {
        if (user.getLastFreezeResetDate() == null ||
            ChronoUnit.DAYS.between(user.getLastFreezeResetDate(), today) >= 7) {

            user.setAvailableFreezes(1);
            user.setLastFreezeResetDate(today);
        }
    }


   
}
