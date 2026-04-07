package com.climbup.service.productivity;

import com.climbup.model.Badge;
import com.climbup.model.User;
import com.climbup.model.UserBadge;
import com.climbup.repository.BadgeRepository;
import com.climbup.repository.UserBadgeRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    public BadgeService(BadgeRepository badgeRepository,
                        UserBadgeRepository userBadgeRepository) {
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

    // ---------------------------------------------------
    // CHECK AND AWARD BADGES
    // ---------------------------------------------------
    public void evaluateBadges(User user,
                               int currentStreak,
                               int completedTasks,
                               int level) {

        // 🔥 Streak Badge
        if (currentStreak >= 7) {
            awardBadgeIfNotExists(user, "7_DAY_STREAK");
        }

        // ✅ Tasks Badge
        if (completedTasks >= 50) {
            awardBadgeIfNotExists(user, "50_TASKS");
        }

        // 🏆 Level Badge
        if (level >= 5) {
            awardBadgeIfNotExists(user, "LEVEL_5");
        }
    }

    // ---------------------------------------------------
    // PREVENT DUPLICATE BADGES
    // ---------------------------------------------------
    private void awardBadgeIfNotExists(User user, String badgeCode) {

        Badge badge = badgeRepository.findByCode(badgeCode)
                .orElseThrow(() -> new RuntimeException("Badge not found"));

        boolean alreadyEarned =
                userBadgeRepository.existsByUserAndBadge(user, badge);

        if (!alreadyEarned) {
            UserBadge userBadge = new UserBadge();
            userBadge.setUser(user);
            userBadge.setBadge(badge);
            userBadge.setEarnedAt(Instant.now());

            userBadgeRepository.save(userBadge);
        }
    }

    // ---------------------------------------------------
    // GET USER BADGES (For Profile)
    // ---------------------------------------------------
    public List<UserBadge> getUserBadges(User user) {
        return userBadgeRepository.findByUser(user);
    }
    public List<Badge> getBadgesForUser(User user) {

        List<UserBadge> userBadges = userBadgeRepository.findByUser(user);

        return userBadges.stream()
                .map(UserBadge::getBadge)
                .collect(Collectors.toList());
    }
}
