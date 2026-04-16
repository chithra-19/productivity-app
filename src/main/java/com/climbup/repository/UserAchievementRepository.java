package com.climbup.repository;

import com.climbup.model.User;

import com.climbup.model.UserAchievement;

import jakarta.transaction.Transactional;

import com.climbup.model.AchievementCode;
import com.climbup.model.AchievementTemplate;
import com.climbup.model.Goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

	Optional<UserAchievement> findByUserAndTemplate(User user, AchievementTemplate template);

    List<UserAchievement> findByUserId(Long userId);

    List<UserAchievement> findByUserAndUnlockedTrue(User user);

    List<UserAchievement> findByUserAndNewlyUnlockedTrue(User user);

    boolean existsByUserAndNewlyUnlockedTrue(User user);

    long countByUser(User user);
    
    @Query("SELECT ua FROM UserAchievement ua JOIN FETCH ua.template WHERE ua.user.id = :userId")
    List<UserAchievement> findByUserIdWithTemplate(@Param("userId") Long userId);
    
    List<UserAchievement> findByUser(User user);
    
    List<UserAchievement> findByGoal(Goal goal);

 // Customized user goals (goal_id is not null)
    List<UserAchievement> findByUserAndGoalIsNotNull(User user);

    // Default template goals (goal_id is null)
    List<UserAchievement> findByUserAndGoalIsNull(User user);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM UserAchievement ua WHERE ua.goal.id = :goalId")
    void deleteByGoalId(@Param("goalId") Long goalId);

    boolean existsByUserAndTemplate(User user, AchievementTemplate template);
    
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user.id = :userId OR ua.user.id = 0")
    List<UserAchievement> findAllVisibleAchievements(@Param("userId") Long userId);
    
}