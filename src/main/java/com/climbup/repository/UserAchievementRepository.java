package com.climbup.repository;

import com.climbup.model.User;
import com.climbup.model.UserAchievement;
import com.climbup.model.AchievementTemplate;
import com.climbup.model.Goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    // ========================
    // BASIC LOOKUPS
    // ========================

    List<UserAchievement> findByUser(User user);

    List<UserAchievement> findByUserId(Long userId);

    Optional<UserAchievement> findById(Long id);

    long countByUser(User user);

    boolean existsByUserId(Long userId);

    // ========================
    // TEMPLATE-BASED
    // ========================

    Optional<UserAchievement> findByUserAndTemplate(User user, AchievementTemplate template);

    boolean existsByUserAndTemplate(User user, AchievementTemplate template);

    List<UserAchievement> findByUserAndUnlockedTrue(User user);

    List<UserAchievement> findByUserAndNewlyUnlockedTrue(User user);

    boolean existsByUserAndNewlyUnlockedTrue(User user);

    // ========================
    // GOAL-BASED (IMPORTANT FOR YOUR SYSTEM)
    // ========================

    List<UserAchievement> findByGoal(Goal goal);

    List<UserAchievement> findByUserAndGoal(User user, Goal goal);

    Optional<UserAchievement> findByGoalId(Long goalId);

    List<UserAchievement> findByUserAndGoalIsNotNull(User user);

    List<UserAchievement> findByUserAndGoalIsNull(User user);

    // 🔥 New method: fetch only locked goal-linked achievements
    List<UserAchievement> findByUserAndGoalIsNotNullAndUnlockedFalse(User user);

    // 🔥 CHECK IF GOAL HAS UNLOCKED ACHIEVEMENT
    boolean existsByUserAndGoalAndUnlockedTrue(User user, Goal goal);

    // ========================
    // OPTIMIZED FETCH (NO N+1)
    // ========================

    @Query("""
        SELECT ua
        FROM UserAchievement ua
        JOIN FETCH ua.template
        WHERE ua.user.id = :userId
    """)
    List<UserAchievement> findAllByUserIdWithTemplate(@Param("userId") Long userId);

    // ========================
    // DELETE OPERATIONS
    // ========================

    @Modifying
    @Transactional
    @Query("DELETE FROM UserAchievement ua WHERE ua.goal.id = :goalId")
    void deleteByGoalId(@Param("goalId") Long goalId);
}
