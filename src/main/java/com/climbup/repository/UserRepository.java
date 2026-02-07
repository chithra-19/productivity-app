package com.climbup.repository;

import com.climbup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by username
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String email);

    long countByLastLoginAtAfter(LocalDateTime dateTime);

    // ✅ Changed to return Optional
    Optional<User> findByEmail(String email);

    // Check if username already exists
    boolean existsByUsername(String username);

    // Check if email already exists
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.tasks WHERE u.username = :username")
    Optional<User> findUserWithTasks(@Param("username") String username);
    
    @Query("""
    	    SELECT u FROM User u
    	    LEFT JOIN FETCH u.tasks
    	    LEFT JOIN FETCH u.goals
    	    LEFT JOIN FETCH u.achievements
    	  
    	    WHERE u.username = :username
    	""")
    	Optional<User> findUserWithAllData(@Param("username") String username);
    
    @Query("""
    	    SELECT u FROM User u
    	    LEFT JOIN FETCH u.tasks
    	    
    	    WHERE u.username = :username
    	""")
    	Optional<User> findUserWithTasksAndChallenges(@Param("username") String username);

}
