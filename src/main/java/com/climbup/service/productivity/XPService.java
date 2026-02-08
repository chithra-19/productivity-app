package com.climbup.service.productivity;

import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class XPService {

    private final UserRepository userRepository;

    // ===== XP RULES (DAY 1) =====
    private static final int XP_PER_TASK = 10;
    private static final int XP_PER_GOAL = 50;     // future-ready
    private static final int XP_PER_STREAK_DAY = 5;

    // Optional level system (kept dead simple)
    private static final int BASE_XP_FOR_LEVEL = 100;

    public XPService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Call this ONLY when a task is completed
     */
    @Transactional
    public void handleTaskCompletion(User user, Task task) {

        if (user == null || task == null) return;

        int xpEarned = XP_PER_TASK;

        // 🔥 streak bonus (simple + visible impact)
        if (user.getCurrentStreak() > 0) {
            xpEarned += XP_PER_STREAK_DAY;
        }

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

        // Optional level logic (does NOT affect dashboard today)
        int oldLevel = user.getLevel();
        int newLevel = calculateLevel(updatedXp);

        if (newLevel > oldLevel) {
            user.setLevel(newLevel);
            onLevelUp(user, oldLevel, newLevel);
        }

        userRepository.save(user);
    }

    /**
     * Very predictable leveling
     */
    public int calculateLevel(int totalXp) {
        return (totalXp / BASE_XP_FOR_LEVEL) + 1;
    }

    /**
     * XP needed to reach next level
     */
    public int xpForNextLevel(User user) {
        int nextLevel = user.getLevel() + 1;
        return (nextLevel - 1) * BASE_XP_FOR_LEVEL;
    }

    /**
     * XP earned inside current level
     */
    public int xpProgressInCurrentLevel(User user) {
        int currentLevelStartXp = (user.getLevel() - 1) * BASE_XP_FOR_LEVEL;
        return user.getXp() - currentLevelStartXp;
    }

    /**
     * Hook for future rewards
     */
    private void onLevelUp(User user, int oldLevel, int newLevel) {
        // Future:
        // - achievements
        // - notifications
        // - animations
        // - rewards

        // Day 1: do nothing (but logic is interview gold)
    }
}
