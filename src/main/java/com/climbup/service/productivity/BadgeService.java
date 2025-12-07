package com.climbup.service.productivity;

import com.climbup.model.Badge;
import com.climbup.model.User;
import com.climbup.repository.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    // Get badges unlocked by user
    public List<Badge> getUserBadges(User user) {
        return badgeRepository.findByUser(user);
    }

    // Award badge if not already unlocked
    public void awardBadge(User user, String badgeCode, String name, String description, String icon) {

        if (badgeRepository.existsByUserAndCode(user, badgeCode)) {
            return;  // Already unlocked
        }

        Badge badge = new Badge();
        badge.setUser(user);
        badge.setCode(badgeCode);
        badge.setName(name);
        badge.setIcon(icon);
        badge.setUnlockedAt(LocalDate.now());

        badgeRepository.save(badge);
    }

    // Count total badges unlocked
    public int countBadges(User user) {
        return badgeRepository.countByUser(user);
    }
}
