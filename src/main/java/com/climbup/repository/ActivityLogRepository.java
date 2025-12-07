package com.climbup.repository;

import com.climbup.model.ActivityLog;
import com.climbup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // 🔹 For category-specific heatmap
    List<ActivityLog> findByUserAndCategoryAndActivityDateBetween(
            User user, String category, LocalDate from, LocalDate to
    );

    Optional<ActivityLog> findByUserAndCategoryAndActivityDate(
            User user, String category, LocalDate date
    );

    // 🔹 For recent activities (latest 10 logs)
    List<ActivityLog> findTop10ByUserOrderByLoggedAtDesc(User user);

    // 🔹 For heatmap date range
    List<ActivityLog> findByUserAndActivityDateBetweenOrderByActivityDateDesc(
            User user, LocalDate from, LocalDate to
    );

    // 🔹 For streak (sorted by date)
    List<ActivityLog> findByUserOrderByActivityDateDesc(User user);

    // 🔹 For unlimited recent logs (sorted by logged time)
    List<ActivityLog> findByUserOrderByLoggedAtDesc(User user);

}
