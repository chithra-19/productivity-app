package com.climbup.repository;

import com.climbup.model.FocusSession;
import com.climbup.model.SessionStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {

    // 🔥 1. FULL HISTORY (sorted - MOST IMPORTANT)
    List<FocusSession> findByUserIdOrderByStartTimeDesc(Long userId);

    // 🔥 2. PAGINATED HISTORY
    Page<FocusSession> findByUserId(Long userId, Pageable pageable);

    // 🔥 3. FILTER BY STATUS (Completed / Aborted / Active)
    List<FocusSession> findByUserIdAndStatusOrderByStartTimeDesc(
            Long userId,
            SessionStatus status
    );

    // 🔥 4. CURRENT ACTIVE SESSION (based on STATUS, not endTime)
    Optional<FocusSession> findTopByUserIdAndStatusOrderByStartTimeDesc(
            Long userId,
            SessionStatus status
    );

    // 🔥 5. TODAY’S SESSIONS
    List<FocusSession> findByUserIdAndStartTimeAfter(
            Long userId,
            OffsetDateTime todayStart
    );

    // 🔥 6. COUNT COMPLETED TODAY
    long countByUserIdAndStatusAndStartTimeAfter(
            Long userId,
            SessionStatus status,
            OffsetDateTime todayStart
    );

    Page<FocusSession> findByStatus(SessionStatus status, Pageable pageable);
    
    List<FocusSession> findByStatus(SessionStatus status);
    
    Page<FocusSession> findByUserIdAndStatus(Long userId, SessionStatus status, Pageable pageable);
    
    // 🔥 7. TOTAL COMPLETED COUNT
    long countByUserIdAndStatus(Long userId, SessionStatus status);

    @Query("""
    	    SELECT COALESCE(SUM(f.elapsedMinutes), 0)
    	    FROM FocusSession f
    	    WHERE f.user.id = :userId
    	    AND f.status = :status
    	""")
    	int sumElapsedMinutesByUserIdAndStatus(Long userId, SessionStatus status);
    
    @Query("""
    	    SELECT COALESCE(SUM(f.elapsedMinutes), 0)
    	    FROM FocusSession f
    	    WHERE f.user.id = :userId
    	    AND f.status IN ('COMPLETED', 'ABORTED')
    	""")
    	int sumElapsedMinutesByUserId(Long userId);
    
}