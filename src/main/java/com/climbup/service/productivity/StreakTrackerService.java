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
import java.util.Set;
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

        userRepository.save(user); // 🔥 THIS IS MISSING

        if (current > oldStreak) {
            activityService.log(
                "🔥 Streak increased to " + current + " days",
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

        Map<LocalDate, Long> counts = taskRepository.findByUserAndCompletedTrue(user)
                .stream()
                .filter(t -> t.getCompletedDateTime() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCompletedDateTime()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        Collectors.counting()
                ));

        Set<LocalDate> validDates = counts.entrySet().stream()
                .filter(e -> e.getValue() >= 4)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        if (validDates.isEmpty()) return 0;

        LocalDate today = LocalDate.now();

        LocalDate cursor = validDates.contains(today)
                ? today
                : today.minusDays(1);

        int streak = 0;

        while (validDates.contains(cursor)) {
            streak++;
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

   

   
}
