package com.climbup.service.productivity;

import com.climbup.dto.request.DailyStats;
import org.springframework.stereotype.Service;

@Service
public class ProductivityScoreService {

    /**
     * Calculate a productivity score (0-100) based on daily stats.
     */
    public int calculateScore(DailyStats stats) {
        if (stats == null) return 0;

        int score = 0;

        // 1️⃣ Focused minutes contribution (50%)
        if (stats.getDailyGoalMinutes() > 0) {
            double focusRatio = (double) stats.getFocusedMinutes() / stats.getDailyGoalMinutes();
            score += Math.min(focusRatio * 50, 50);
        }

        // 2️⃣ Task completion contribution (30%)
        if (stats.getPlannedTasks() > 0) {
            double taskRatio = (double) stats.getCompletedTasks() / stats.getPlannedTasks();
            score += Math.min(taskRatio * 30, 30);
        }

        // 3️⃣ Session quality contribution (20%)
        int sessionScore = 20;
        if (stats.isQuitEarly()) sessionScore -= 10;
        if (stats.isIdleDetected()) sessionScore -= 10;
        if (stats.getTabSwitches() > 3) sessionScore -= 5;

        score += Math.max(sessionScore, 0);

        return Math.min(score, 100);
    }
}