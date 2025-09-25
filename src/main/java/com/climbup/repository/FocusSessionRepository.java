package com.climbup.repository;

import com.climbup.model.FocusSession;
import com.climbup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {

    // 🔹 Get all sessions for a specific user
    List<FocusSession> findByUser(User user);

    // 🔹 Get only successful sessions for a user
    List<FocusSession> findByUserAndSuccessfulTrue(User user);

    // 🔹 Count all successful sessions for a user
    long countByUserAndSuccessfulTrue(User user);

    // 🔹 Total focus minutes for a user
    List<FocusSession> findByUserOrderByStartTimeDesc(User user);
}
