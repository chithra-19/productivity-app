package com.climbup.repository;

import com.climbup.model.User;
import com.climbup.model.Badge;
import com.climbup.model.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    // Check if user already has a specific badge
    boolean existsByUserAndBadge(User user, Badge badge);

    // Get all badges of a user
    List<UserBadge> findByUser(User user);

    // Optional: get specific user-badge mapping
    Optional<UserBadge> findByUserAndBadge(User user, Badge badge);
}
