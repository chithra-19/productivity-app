package com.climbup.repository;

import com.climbup.model.FocusSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.climbup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {

    // Get all sessions for a user that started today
	@Query("""
		    SELECT f FROM FocusSession f
		    WHERE f.user.id = :userId AND f.startTime >= :todayStart
		""")
		List<FocusSession> findTodaySessions(Long userId, LocalDateTime todayStart);
	
    // Count successful sessions for today
	@Query("""
		    SELECT COUNT(f) FROM FocusSession f
		    WHERE f.user.id = :userId 
		    AND f.successful = true 
		    AND f.startTime >= :todayStart
		""")
		Long countCompletedToday(Long userId, LocalDateTime todayStart);
	
	@Query("""
		    SELECT f FROM FocusSession f
		    WHERE f.user.id = :userId AND f.successful = false
		    ORDER BY f.startTime DESC
		""")
		List<FocusSession> findCurrentSessions( Long userId);

    
    @Query("""
    	    SELECT f FROM FocusSession f
    	    WHERE f.user.id = :userId AND f.endTime IS NULL
    	    ORDER BY f.startTime DESC
    	""")
    	Optional<FocusSession> findActiveSession(Long userId);
    
    List<FocusSession> findByUserId(Long userId);
    
    Page<FocusSession> findByUserId(Long userId, Pageable pageable);
    
    List<FocusSession> findByUserIdAndSuccessfulTrue(Long userId);
    

    long countByUserIdAndSuccessfulTrue(Long userId);
    
    Optional<FocusSession> findTopByUserIdAndEndTimeIsNullOrderByStartTimeDesc(Long userId);
    }
