package com.climbup.repository;

import com.climbup.model.StreakChallenge;
import com.climbup.model.StreakChallenge.ChallengeType;
import com.climbup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StreakChallengeRepository extends JpaRepository<StreakChallenge, Long> {

    List<StreakChallenge> findByUser(User user);

    List<StreakChallenge> findByUserAndCompletedFalse(User user);

    boolean existsByUserAndChallengeTypeAndCompletedFalse(User user, StreakChallenge.ChallengeType type);

	Optional<StreakChallenge> findByUserAndChallengeType(User user, ChallengeType type);
}
