package com.climbup.service.productivity;

import com.climbup.model.StreakChallenge;
import com.climbup.repository.StreakChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StreakAutoResetService {

    @Autowired
    private StreakChallengeRepository streakRepository;

    /**
     * Runs every day at 00:05 AM and resets streaks if the last completed date
     * is before yesterday.
     */
    @Scheduled(cron = "0 5 0 * * ?") // every day at 00:05 AM
    public void resetMissedStreaks() {
        List<StreakChallenge> streaks = streakRepository.findAll();

        for (StreakChallenge streak : streaks) {
            LocalDate lastDate = streak.getLastCompletedDate();

            if (lastDate != null && lastDate.isBefore(LocalDate.now().minusDays(1))) {
                streak.resetStreak();
                streakRepository.save(streak);
            }
        }
    }
}
