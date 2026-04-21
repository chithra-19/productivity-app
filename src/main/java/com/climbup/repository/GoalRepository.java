package com.climbup.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.climbup.model.Goal;
import com.climbup.model.GoalStatus;
import com.climbup.model.User;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUserEmail(String email);

    List<Goal> findByUserEmailAndStatus(String email, GoalStatus status);

    List<Goal> findByUser(User user);

    List<Goal> findByUserAndStatus(User user, GoalStatus status);

    Optional<Goal> findByIdAndUser(Long id, User user);
    
    long countByUserAndCompletedTrue(User user);

    @Query("""
    		SELECT DISTINCT g 
    		FROM Goal g 
    		LEFT JOIN FETCH g.userAchievements ua
    		LEFT JOIN FETCH ua.template
    		WHERE g.user.id = :userId
    		""")
    		List<Goal> findAllByUserWithAchievements(@Param("userId") Long userId);
    long countByUserAndStatus(User user, GoalStatus status);
}