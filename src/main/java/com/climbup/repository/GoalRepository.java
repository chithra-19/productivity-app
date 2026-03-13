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

   
    // Find all goals of a user by email
    List<Goal> findByUserEmail(String email);

    // Find all goals of a user with a given status (ACTIVE, COMPLETED, DROPPED)
    List<Goal> findByUserEmailAndStatus(String email, GoalStatus status);

 
    // Find all goals belonging to a User entity
    List<Goal> findByUser(User user);

  
    Optional<Goal> findByIdAndUser(Long id, User user);

    @Query("SELECT g FROM Goal g LEFT JOIN FETCH g.achievements WHERE g.user.id = :userId")
    List<Goal> findAllByUserWithAchievements(@Param("userId") Long userId);

}
