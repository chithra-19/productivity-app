package com.climbup.repository;

import com.climbup.model.Activity;
import com.climbup.model.User;
import com.climbup.model.Activity.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // 1. All activities for a user
    List<Activity> findByUser(User user);

    // 1b. Paged activities for a user
    Page<Activity> findByUser(User user, Pageable pageable);

    // 2. All activities of a type for a user
    List<Activity> findByUserAndType(User user, ActivityType type);

    List<Activity> findTop10ByOrderByTimestampDesc();

    List<Activity> findByUserOrderByTimestampDesc(User user);
    // 3. Activities between two timestamps
    List<Activity> findByUserAndTimestampBetween(
            User user,
            LocalDateTime from,
            LocalDateTime to
    );

    // 4. Activities of a type between timestamps
    List<Activity> findByUserAndTypeAndTimestampBetween(
            User user,
            ActivityType type,
            LocalDateTime from,
            LocalDateTime to
    );
    
    
}
