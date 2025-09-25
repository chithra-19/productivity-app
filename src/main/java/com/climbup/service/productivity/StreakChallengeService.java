package com.climbup.service.productivity;

import com.climbup.dto.request.UpdateChallengeRequestDTO;
import com.climbup.model.StreakChallenge;
import com.climbup.model.User;
import com.climbup.repository.StreakChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StreakChallengeService {

    private final StreakChallengeRepository repository;

    @Autowired
    public StreakChallengeService(StreakChallengeRepository repository) {
        this.repository = repository;
    }

    // ---------- Start a challenge ----------
    public StreakChallenge startChallenge(User user, StreakChallenge.ChallengeType type) {
        StreakChallenge challenge = new StreakChallenge();
        challenge.setUser(user);
        challenge.setChallengeType(type);
        challenge.setStartDate(LocalDate.now());
        challenge.setCurrentStreak(0);
        challenge.setCompleted(false);
        return repository.save(challenge);
    }

    // ---------- Mark today completed ----------
    public StreakChallenge markTodayCompleted(User user, StreakChallenge.ChallengeType type) {
        StreakChallenge challenge = getChallenge(user, type)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        // Increment streak if not already completed
        if (!challenge.isCompleted()) {
            challenge.setCurrentStreak(challenge.getCurrentStreak() + 1);
            challenge.setCompleted(true);
        }

        return repository.save(challenge);
    }

    // ---------- Reset challenge ----------
    public StreakChallenge resetChallenge(User user, StreakChallenge.ChallengeType type) {
        StreakChallenge challenge = getChallenge(user, type)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        challenge.setCurrentStreak(0);
        challenge.setCompleted(false);
        return repository.save(challenge);
    }

    // ---------- Get all challenges for user ----------
    public List<StreakChallenge> getChallengesForUser(User user) {
        return repository.findByUser(user);
    }

    // ---------- Get single challenge ----------
    public Optional<StreakChallenge> getChallenge(User user, StreakChallenge.ChallengeType type) {
        return repository.findByUserAndChallengeType(user, type);
    }

    // ---------- Update challenge ----------
    public StreakChallenge updateChallenge(User user, StreakChallenge.ChallengeType type, UpdateChallengeRequestDTO dto) {
        StreakChallenge challenge = getChallenge(user, type)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        if (dto.getType() != null) {
            challenge.setChallengeType(dto.getType());
        }
        if (dto.getCompleted() != null) {
            challenge.setCompleted(dto.getCompleted());
        }
        if (dto.getStreakCount() != null) {
            challenge.setCurrentStreak(dto.getStreakCount());
        }

        return repository.save(challenge);
    }
}
