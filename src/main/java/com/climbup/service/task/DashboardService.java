package com.climbup.service.task;

import com.climbup.dto.response.DashboardResponseDTO;
import com.climbup.model.User;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.productivity.XPService;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final StreakTrackerService streakTrackerService;
    private final XPService xpService;

    public DashboardService(StreakTrackerService streakTrackerService,
                            XPService xpService) {

        this.streakTrackerService = streakTrackerService;
        this.xpService = xpService;
    }

    public DashboardResponseDTO buildDashboard(User user) {

        DashboardResponseDTO dto = new DashboardResponseDTO();

        // ===== USER INFO =====
        dto.setFirstName(
                user.getProfile() != null
                        ? user.getProfile().getFirstName()
                        : "User"
        );

        // ===== STREAK =====
        dto.setCurrentStreak(streakTrackerService.getCurrentStreak(user));
        dto.setBestStreak(streakTrackerService.getBestStreak(user));

        // ===== XP =====
        long xp = xpService.getCurrentXP(user.getId());
        int level = xpService.getLevel(user.getId());

        dto.setLevel(level);
        dto.setXp((int) xp);

        dto.setXpProgress(xpService.xpProgressInCurrentLevel(user));
        dto.setXpForNextLevel(xpService.xpRequiredForNextLevel(level));

        // ===== PRODUCTIVITY =====
        int productivityScore = clamp(user.getProductivityScore());

        dto.setProductivityScore(productivityScore);
        dto.setProductivityLabel(mapLabel(productivityScore));

        return dto;
    }

    // =========================
    // HELPERS
    // =========================

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    private String mapLabel(int score) {
        if (score < 40) return "LOW";
        if (score < 70) return "MEDIUM";
        return "HIGH";
    }
}