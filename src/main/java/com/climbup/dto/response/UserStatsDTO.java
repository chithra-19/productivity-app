package com.climbup.dto.response;

public class UserStatsDTO {

    private int currentStreak;
    private int bestStreak;

    public UserStatsDTO(int currentStreak, int bestStreak) {
        this.currentStreak = currentStreak;
        this.bestStreak = bestStreak;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public int getBestStreak() {
        return bestStreak;
    }
}
