package com.climbup.service.productivity;

import com.climbup.model.User;
import com.climbup.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class XPService {

    private final UserRepository userRepository;
    private final StreakTrackerService streakTrackerService;

    // Level system: level 1 -> 2 needs 100 XP, then each level needs 50 more than the last
    private static final int BASE_XP_FOR_LEVEL = 100;
    private static final int XP_STEP_PER_LEVEL = 50;

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

        int oldLevel = calculateLevel(user.getXp());

        int updatedXp = user.getXp() + xpToAdd;
        user.setXp(updatedXp);

        int newLevel = calculateLevel(updatedXp);
        user.setLevel(newLevel);

        if (newLevel > oldLevel) {
            onLevelUp(user, oldLevel, newLevel);
        }

        userRepository.save(user);
    }

    /**
     * Take XP back (used when a completed task is re-opened)
     */
    @Transactional
    public void removeXp(User user, int xpToRemove) {

        if (user == null || xpToRemove <= 0) return;

        int updatedXp = Math.max(0, user.getXp() - xpToRemove);
        user.setXp(updatedXp);
        user.setLevel(calculateLevel(updatedXp));

        userRepository.save(user);
    }

    // =============================
    // LEVEL FORMULA (one place only)
    // =============================

    /** XP needed to go from this level to the next one */
    public int xpRequiredForNextLevel(int level) {
        return BASE_XP_FOR_LEVEL + (level - 1) * XP_STEP_PER_LEVEL;
    }

    /** Total XP at which this level starts (level 1 starts at 0) */
    private int xpAtLevelStart(int level) {
        int n = level - 1;
        return BASE_XP_FOR_LEVEL * n + XP_STEP_PER_LEVEL * n * (n - 1) / 2;
    }

    /** Level for a given total XP */
    public int calculateLevel(int totalXp) {
        int level = 1;
        while (totalXp >= xpAtLevelStart(level + 1)) {
            level++;
        }
        return level;
    }

    /** XP needed for next level (for this user's current level) */
    public int xpForNextLevel(User user) {
        return xpRequiredForNextLevel(calculateLevel(user.getXp()));
    }

    /** XP earned inside the current level */
    public int xpProgressInCurrentLevel(User user) {
        int level = calculateLevel(user.getXp());
        return user.getXp() - xpAtLevelStart(level);
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
        return calculateLevel(user.getXp());
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
        int required = xpForNextLevel(user);

        return (progress * 100.0) / required;
    }

    public int getProgressToNextLevel(long xp) {
        int level = calculateLevel((int) xp);
        int progress = (int) xp - xpAtLevelStart(level);
        return progress * 100 / xpRequiredForNextLevel(level);
    }

    public int calculateXpProgress(User user) {
        return xpProgressInCurrentLevel(user);
    }
}