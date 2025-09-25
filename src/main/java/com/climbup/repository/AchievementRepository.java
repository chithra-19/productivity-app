package com.climbup.repository;

import com.climbup.model.Achievement;
import com.climbup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    // Count all achievements for a specific user
    long countByUser(User user);

    // Get all achievements for a user
    List<Achievement> findByUser(User user);

    // Get achievements by user and unlocked status
    List<Achievement> findByUserAndUnlocked(User user, boolean unlocked);

    // Check if the user has any unlocked achievements
    boolean existsByUserAndUnlockedTrue(User user);

    // Get all newly unlocked (not yet seen) achievements for a user
    List<Achievement> findByUserAndNewlyUnlockedTrue(User user);
}
