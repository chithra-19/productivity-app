package com.climbup.repository;

import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.model.Task.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // 📑 Pagination
    Page<Task> findByUser(User user, Pageable pageable);
    Page<Task> findByUserId(Long userId, Pageable pageable);

    // 📊 Counts
    long countByUser(User user);
    long countByUserAndCompleted(User user, boolean completed);

    // Use JPQL to count explicitly
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = :user AND t.completed = true")
    long countCompletedTasksByUser(@Param("user") User user);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = :user AND t.completed = false")
    long countPendingTasksByUser(@Param("user") User user);

    // 🔍 Basic
    List<Task> findByUser(User user);
    List<Task> findByUserAndCompleted(User user, boolean completed);
    List<Task> findByUserAndCompletedTrue(User user);
    List<Task> findByUserAndCompletedFalse(User user);
    Optional<Task> findByIdAndUser(Long id, User user);
    Optional<Task> findByIdAndUserId(Long id, Long userId);
    boolean existsByIdAndUserId(Long id, Long userId);

    // 📅 Date-based
    List<Task> findByUserAndDueDate(User user, LocalDate dueDate);
    List<Task> findByUserAndDueDateBetween(User user, LocalDate start, LocalDate end);
    List<Task> findByUserAndDueDateAfter(User user, LocalDate date);
    List<Task> findByUserAndDueDateBefore(User user, LocalDate date);

    // ✅ Consistent with AchievementService (use completedDateTime in Task entity)
    List<Task> findByUserAndCompletedTrueOrderByCompletedDateTimeDesc(User user);

    // ⏰ Time-based
    List<Task> findByUserAndStartTimeBetween(User user, LocalTime start, LocalTime end);

    // 🚩 Missed / Overdue
    List<Task> findByUserAndMissedTrue(User user);

    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.dueDate < :today AND t.completed = false")
    List<Task> findOverdueTasks(@Param("user") User user, @Param("today") LocalDate today);

    // 🏆 Priority
    List<Task> findByUserAndPriority(User user, Priority priority);
    List<Task> findByUserAndPriorityIn(User user, List<Priority> priorities);

    // 🔥 Productivity (use completedDateTime)
    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.completed = true AND t.completedDateTime BETWEEN :start AND :end")
    List<Task> findCompletedTasksBetweenDates(@Param("user") User user,
                                              @Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

    // ⚡ Bulk operations
    @Modifying
    @Query("UPDATE Task t SET t.completed = true, t.completedDateTime = :completionDate WHERE t.id = :id AND t.user = :user")
    int markTaskAsCompleted(@Param("id") Long id,
                            @Param("user") User user,
                            @Param("completionDate") LocalDateTime completionDate);

    @Modifying
    @Query("UPDATE Task t SET t.missed = true WHERE t.user = :user AND t.dueDate < :today AND t.completed = false")
    int markOverdueTasks(@Param("user") User user, @Param("today") LocalDate today);

    // 🔍 Search (fix: Task has "title", not "name")
    @Query("SELECT t FROM Task t WHERE t.user = :user AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Task> searchTasks(@Param("user") User user, @Param("query") String query);
}
