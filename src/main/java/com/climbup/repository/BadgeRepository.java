package com.climbup.repository;

import com.climbup.model.Badge;
import com.climbup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    List<Badge> findByUser(User user);

    boolean existsByUserAndCode(User user, String code);

    int countByUser(User user);
}
