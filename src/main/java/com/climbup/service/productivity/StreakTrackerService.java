package com.climbup.service.productivity;

import com.climbup.model.ActivityType;
import com.climbup.model.StreakTracker;
import com.climbup.model.User;
import com.climbup.repository.StreakTrackerRepository;
import com.climbup.repository.TaskRepository;
import com.climbup.repository.UserRepository;
import com.climbup.service.task.ActivityService;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StreakTrackerService {

    /**
     * How many tasks must be completed in a day for that day to count towards a streak.
     * Used by the current streak, the best streak and the consistency score,
     * so they can never disagree with each other.
     */
    public static final int DAILY_TASK_TARGET = 4;

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

    // 🔹 Get all streaks for a user
    public List<StreakTracker> getAllStreaksForUser(Long userId) {
        return streakTrackerRepository.findAllByUserId(userId);
    }

    public void refreshUserStreak(User user) {

        int oldStreak = user.getCurrentStreak();

        int current = getCurrentStreak(user);
        int best = getBestStreak(user);

        user.setCurrentStreak(current);
        user.setBestStreak(best);

        userRepository.save(user);

        if (current > oldStreak) {
            activityService.log(
                "🔥 Streak increased to " + current + " days",
                ActivityType.STREAK_UPDATED,
                user
            );
        }
    }

    // =========================================================
    // SHARED HELPERS (single definition of "a streak day")
    // =========================================================

    /** Number of tasks completed per calendar day (server time zone). */
    private Map<LocalDate, Long> completedCountsByDate(User user) {
        return taskRepository.findByUserAndCompletedTrue(user)
                .stream()
                .filter(t -> t.getCompletedDateTime() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCompletedDateTime()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        Collectors.counting()
                ));
    }

    /** Days on which the daily target was reached. */
    private Set<LocalDate> streakDates(User user) {
        return completedCountsByDate(user).entrySet().stream()
                .filter(e -> e.getValue() >= DAILY_TASK_TARGET)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public int getBestStreak(User user) {

        List<LocalDate> dates = streakDates(user)
                .stream()
                .sorted()
                .toList();

        if (dates.isEmpty()) return 0;

        int best = 1, run = 1;

        for (int i = 1; i < dates.size(); i++) {
            if (dates.get(i).equals(dates.get(i - 1).plusDays(1))) {
                run++;
            } else {
                best = Math.max(best, run);
                run = 1;
            }
        }

        return Math.max(best, run);
    }

    public int getCurrentStreak(User user) {

        Set<LocalDate> validDates = streakDates(user);

        if (validDates.isEmpty()) return 0;

        LocalDate today = LocalDate.now();

        // A streak is still alive if today OR yesterday was a streak day
        LocalDate cursor = validDates.contains(today)
                ? today
                : today.minusDays(1);

        int streak = 0;

        int freezes = user.getAvailableFreezes();

        while (true) {

            if (validDates.contains(cursor)) {
                streak++;
            } else if (freezes > 0) {
                freezes--; // ❄️ use freeze
            } else {
                break;
            }

            cursor = cursor.minusDays(1);
        }

        return streak;
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

    public List<Map<String, Object>> getMonthlyHeatmap(User user, YearMonth yearMonth) {

        LocalDate startOfMonth = yearMonth.atDay(1);

        Instant start = startOfMonth
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        Map<LocalDate, Long> counts = taskRepository
                .findCompletedTasksFromDate(user, start)
                .stream()
                .filter(t -> t.getCompletedDateTime() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCompletedDateTime()
                              .atZone(ZoneId.systemDefault())
                              .toLocalDate(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
            LocalDate date = startOfMonth.withDayOfMonth(i);

            long count = counts.getOrDefault(date, 0L);

            String level;

            if (count == 0) level = "level-0";
            else if (count <= 2) level = "level-1";
            else if (count <= 4) level = "level-2";
            else level = "level-3";

            result.add(Map.of(
                    "date", date,
                    "count", count,
                    "level", level
            ));
        }

        return result;
    }

    public int getConsistencyScore(User user) {

        Instant start = LocalDate.now()
                .minusDays(29)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        Map<LocalDate, Long> counts = taskRepository
                .findCompletedTasksFromDate(user, start)
                .stream()
                .filter(t -> t.getCompletedDateTime() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCompletedDateTime()
                              .atZone(ZoneId.systemDefault())
                              .toLocalDate(),
                        Collectors.counting()
                ));

        int activeDays = 0;

        for (int i = 0; i < 30; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            if (counts.getOrDefault(date, 0L) >= DAILY_TASK_TARGET) {
                activeDays++;
            }
        }

        return (activeDays * 100) / 30;
    }
}
