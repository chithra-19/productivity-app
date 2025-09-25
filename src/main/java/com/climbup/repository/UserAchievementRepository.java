package com.climbup.repository;

import com.climbup.model.UserAchievement;
import com.climbup.model.User;
import com.climbup.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    // 🧠 Check if a user has unlocked a specific achievement
    Optional<UserAchievement> findByUserAndAchievement(User user, Achievement achievement);

    // 📜 Get all achievements unlocked by a user
    List<UserAchievement> findByUser(User user);
}