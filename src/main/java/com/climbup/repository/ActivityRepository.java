package com.climbup.repository;

import com.climbup.model.Activity;
import com.climbup.model.User;
import com.climbup.model.Activity.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByUser(User user);

    List<Activity> findByUserOrderByTimestampDesc(User user);

    List<Activity> findByUserAndType(User user, ActivityType type);

    List<Activity> findByUserAndTimestampBetween(User user, LocalDateTime from, LocalDateTime to);

    List<Activity> findByUserAndTypeAndTimestampBetween(User user, ActivityType type, LocalDateTime from, LocalDateTime to);

    Page<Activity> findByUser(User user, Pageable pageable);
}
