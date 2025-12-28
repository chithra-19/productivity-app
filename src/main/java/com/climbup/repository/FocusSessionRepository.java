package com.climbup.repository;

import com.climbup.model.FocusSession;
import com.climbup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {

    // Get all sessions for a user that started today
    @Query("SELECT f FROM FocusSession f WHERE f.user = :user AND f.startTime >= :todayStart")
    List<FocusSession> findTodaySessions(@Param("user") User user, @Param("todayStart") LocalDateTime todayStart);

    // Count successful sessions for today
    @Query("SELECT COUNT(f) FROM FocusSession f WHERE f.user = :user AND f.successful = true AND f.startTime >= :todayStart")
    Long countCompletedToday(@Param("user") User user, @Param("todayStart") LocalDateTime todayStart);

    @Query("""
    		SELECT f FROM FocusSession f
    		WHERE f.user = :user AND f.successful = false
    		ORDER BY f.startTime DESC
    		""")
    		List<FocusSession> findCurrentSessions(@Param("user") User user);

    List<FocusSession> findByUser(User user);
    
    
    @Query("""
    	    SELECT f FROM FocusSession f
    	    WHERE f.user = :user AND f.endTime IS NULL
    	    ORDER BY f.startTime DESC
    	""")
    	Optional<FocusSession> findActiveSession(@Param("user") User user);

    
    

}
