package com.climbup.repository;

import com.climbup.model.Achievement;
import com.climbup.model.User;
import com.climbup.model.Achievement.AchievementCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    // All achievements of a user
    List<Achievement> findByUser(User user);

    // Achievements by user & unlocked status
    List<Achievement> findByUserAndUnlocked(User user, boolean unlocked);

    // Newly unlocked achievements
    List<Achievement> findByUserAndNewlyUnlockedTrue(User user);

    // Single achievement by user and code
    Optional<Achievement> findByUserAndCode(User user, AchievementCode code);

    // Check if a code exists (used in seeder)
    boolean existsByCode(AchievementCode code);

    // Count by user (used for initialization)
    long countByUser(User user);
}
