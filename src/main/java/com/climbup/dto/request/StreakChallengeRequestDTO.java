package com.climbup.dto.request;

import com.climbup.model.StreakChallenge;
import jakarta.validation.constraints.NotNull;

public class StreakChallengeRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Challenge type is required")
    private StreakChallenge.ChallengeType type;

    public StreakChallengeRequestDTO() {}

    public StreakChallengeRequestDTO(Long userId, StreakChallenge.ChallengeType type) {
        this.userId = userId;
        this.type = type;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public StreakChallenge.ChallengeType getType() { return type; }
    public void setType(StreakChallenge.ChallengeType type) { this.type = type; }
}
