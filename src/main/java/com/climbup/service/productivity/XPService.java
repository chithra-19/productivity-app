package com.climbup.service.productivity;

import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class XPService {

    private final UserRepository userRepository;
    private final StreakTrackerService streakTrackerService;



    // Level system
    private static final int BASE_XP_FOR_LEVEL = 100;

    public XPService(UserRepository userRepository,
                     StreakTrackerService streakTrackerService) {
        this.userRepository = userRepository;
        this.streakTrackerService = streakTrackerService;
    }

    /**
     * Call this ONLY when a task is completed
     * NOW: priority-based XP system
     */
    @Transactional
    public void handleTaskCompletion(User user, int xpEarned) {

        if (user == null) return;

        addXp(user, xpEarned);
    }
    /**
     * Core XP logic (single source of truth)
     */
    @Transactional
    public void addXp(User user, int xpToAdd) {

        if (user == null || xpToAdd <= 0) return;

        int updatedXp = user.getXp() + xpToAdd;
        user.setXp(updatedXp);

        int oldLevel = user.getLevel();
        int newLevel = calculateLevel(updatedXp);

        if (newLevel > oldLevel) {
            user.setLevel(newLevel);
            onLevelUp(user, oldLevel, newLevel);
        }

        userRepository.save(user);
    }

    /**
     * Level calculation
     */
    public int calculateLevel(int totalXp) {
        return (totalXp / BASE_XP_FOR_LEVEL) + 1;
    }

    /**
     * XP needed for next level
     */
    public int xpForNextLevel(User user) {
        int nextLevel = user.getLevel() + 1;
        return (nextLevel - 1) * BASE_XP_FOR_LEVEL;
    }

    /**
     * XP progress inside current level
     */
    public int xpProgressInCurrentLevel(User user) {
        int currentLevelStartXp = (user.getLevel() - 1) * BASE_XP_FOR_LEVEL;
        return user.getXp() - currentLevelStartXp;
    }

    /**
     * Hook for future gamification
     */
    private void onLevelUp(User user, int oldLevel, int newLevel) {
        // future: badges, animations, rewards
    }

    // =============================
    // READ METHODS (Dashboard)
    // =============================

    public int getCurrentXP(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getXp();
    }

    public int getLevel(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getLevel();
    }

    public int getXpForNextLevel(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return xpForNextLevel(user);
    }

    public double getXpPercentage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int progress = xpProgressInCurrentLevel(user);
        int required = BASE_XP_FOR_LEVEL;

        return (progress * 100.0) / required;
    }

    /**
     * Total XP required to reach a level
     */
    private int xpForLevel(int level) {
        return (level - 1) * BASE_XP_FOR_LEVEL;
    }

    public int getProgressToNextLevel(long xp) {
        int currentLevel = calculateLevel((int) xp);

        int xpForCurrentLevel = xpForLevel(currentLevel);
        int xpForNextLevel = xpForLevel(currentLevel + 1);

        int progress = (int) (((double) (xp - xpForCurrentLevel)
                / (xpForNextLevel - xpForCurrentLevel)) * 100);

        return Math.min(progress, 100);
    }

    public int calculateXpProgress(User user) {
        return xpProgressInCurrentLevel(user);
    }
}