package com.climbup.dto.response;

import java.time.LocalDate;

public class StreakChallengeResponseDTO {

    private Long id;
    private int durationDays;
    private int currentStreak;
    private boolean completed;
    private LocalDate startDate;
    private LocalDate lastCompletedDate;

    public StreakChallengeResponseDTO() {}

    public StreakChallengeResponseDTO(Long id, int durationDays, int currentStreak,
                                      boolean completed, LocalDate startDate, LocalDate lastCompletedDate) {
        this.id = id;
        this.durationDays = durationDays;
        this.currentStreak = currentStreak;
        this.completed = completed;
        this.startDate = startDate;
        this.lastCompletedDate = lastCompletedDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getLastCompletedDate() { return lastCompletedDate; }
    public void setLastCompletedDate(LocalDate lastCompletedDate) { this.lastCompletedDate = lastCompletedDate; }
}
