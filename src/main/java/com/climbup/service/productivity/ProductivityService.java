package com.climbup.service.productivity;

import com.climbup.dto.request.DailyStats;
import com.climbup.model.ProductivityScore;
import com.climbup.model.User;
import com.climbup.repository.ProductivityScoreRepository;
import com.climbup.service.productivity.StreakTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ProductivityService {

    @Autowired
    private ProductivityScoreRepository repository;

    @Autowired
    private ProductivityScoreService scoreService;

    @Autowired
    private StreakTrackerService streakService;

    /**
     * Calculate, save, and update streak for a user.
     */
    public ProductivityScore finalizeDailyScore(User user, DailyStats stats) {
        LocalDate today = stats.getDate() != null ? stats.getDate() : LocalDate.now();

        // 1️⃣ Calculate productivity score
        int score = scoreService.calculateScore(stats);

        // 2️⃣ Save or update today's score
        ProductivityScore daily = repository
                .findByUserAndDate(user, today)
                .orElse(new ProductivityScore());

        daily.setUser(user);
        daily.setDate(today);
        daily.setProductivityScore(score);

        // optional: store detailed stats for analytics
        daily.setFocusedMinutes(stats.getFocusedMinutes());
        daily.setDailyGoalMinutes(stats.getDailyGoalMinutes());
        daily.setSessionCount(stats.getSessionCount());
        daily.setPlannedTasks(stats.getPlannedTasks());
        daily.setCompletedTasks(stats.getCompletedTasks());
        daily.setQuitEarly(stats.isQuitEarly());
        daily.setTabSwitches(stats.getTabSwitches());
        daily.setIdleDetected(stats.isIdleDetected());

        repository.save(daily);

        // 3️⃣ Update streak (threshold: 60+ score or 70% daily goal)
        boolean qualified = score >= 60 || stats.getFocusedMinutes() >= stats.getDailyGoalMinutes() * 0.7;
        streakService.updateStreak(user, "Focus", qualified);

        return daily;
    }
}
