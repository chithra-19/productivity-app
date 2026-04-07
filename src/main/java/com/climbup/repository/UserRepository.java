package com.climbup.repository;

import com.climbup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ========================
    // Basic Queries
    // ========================

    Optional<User> findByEmail(String email);
    
//    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.email = :email")
//    User getUserWithAllData(@Param("email") String email);
//    
    @Query("SELECT u FROM User u JOIN FETCH u.profile WHERE u.email = :email")
    Optional<User> findByEmailWithProfile(@Param("email") String email);
 
    boolean existsByEmail(String email);

    long countByLastLoginAtAfter(Instant dateTime);


    // ========================
    // Fetch Optimized Queries
    // ========================

    /**
     * Fetch user along with tasks
     */
    @Query("""
            SELECT DISTINCT u FROM User u
            LEFT JOIN FETCH u.tasks
            WHERE u.email = :email
           """)
    Optional<User> findUserWithTasks(@Param("email") String email);


    /**
     * Fetch user with all associated collections
     */
    @Query("""
    	    SELECT DISTINCT u FROM User u
    	    LEFT JOIN FETCH u.profile
    	    LEFT JOIN FETCH u.tasks
    	    LEFT JOIN FETCH u.goals
    	    LEFT JOIN FETCH u.achievements
    	    WHERE u.email = :email
    	""")
    	Optional<User> findUserWithAllData(@Param("email") String email);
}