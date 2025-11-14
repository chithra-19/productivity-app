package com.climbup.repository;

import com.climbup.model.Achievement;
import com.climbup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    long countByUser(User user);

    List<Achievement> findByUser(User user);

    List<Achievement> findByUserAndUnlocked(User user, boolean unlocked);

    boolean existsByUserAndUnlockedTrue(User user);

    boolean existsByUserAndTitle(User user, String title);
 
    Optional<Achievement> findByUserAndTitle(User user, String title);  // ✅ Add this line

    List<Achievement> findByUserAndNewlyUnlockedTrue(User user);

    Optional<Achievement> findByCode(String code);
    
    Optional<Achievement> findByUserIdAndCode(Long userId, String code);

	Optional<Achievement> findByTitleAndUser(String title, User user);

}
