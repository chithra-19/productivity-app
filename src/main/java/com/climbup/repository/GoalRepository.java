package com.climbup.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.climbup.model.Goal;
import com.climbup.model.User;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    // Find all goals of a user by username
    List<Goal> findByUserUsername(String username);

    // Find all goals of a user by email
    List<Goal> findByUserEmail(String email);

    // Find all goals of a user with a given status (ACTIVE, COMPLETED, DROPPED)
    List<Goal> findByUserUsernameAndStatus(String username, Goal.GoalStatus status);

    // Order user’s goals by due date (soonest first)
    List<Goal> findByUserUsernameOrderByDueDateAsc(String username);

    // Find all goals belonging to a User entity
    List<Goal> findByUser(User user);

    // ✅ NEW — find all ACTIVE (not completed) goals
    List<Goal> findByUserAndCompletedFalse(User user);

    // Find a specific goal by ID and username (ownership check)
    Optional<Goal> findByIdAndUser_Username(Long id, String username);
    
    @Query("SELECT g FROM Goal g LEFT JOIN FETCH g.achievements WHERE g.user.id = :userId")
    List<Goal> findAllByUserWithAchievements(@Param("userId") Long userId);

}
