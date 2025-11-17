package com.climbup.repository;

import com.climbup.model.StreakTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StreakTrackerRepository extends JpaRepository<StreakTracker, Long> {

    // Find a streak by user ID
    Optional<StreakTracker> findByUserId(Long userId);

    // Find a streak by user ID and category
    Optional<StreakTracker> findByUserIdAndCategory(Long userId, String category);

    // Get all streaks for a user
    List<StreakTracker> findAllByUserId(Long userId);

    // ✔ Correct way to get max streak
    @Query("SELECT MAX(s.longestStreak) FROM StreakTracker s WHERE s.user.id = :userId")
    Optional<Integer> getUserBestStreak(Long userId);

    // ✔ Sort by lastActiveDate (this field EXISTS)
    List<StreakTracker> findByUserIdOrderByLastActiveDateAsc(Long userId);
}
