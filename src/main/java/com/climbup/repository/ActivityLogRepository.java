package com.climbup.repository;

import com.climbup.model.ActivityLog;
import com.climbup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByUserAndCategoryAndActivityDateBetween(
        User user, String category, LocalDate start, LocalDate end);
}