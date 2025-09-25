package com.climbup.dto.request;

import com.climbup.model.StreakChallenge;

public class UpdateChallengeRequestDTO {

    private StreakChallenge.ChallengeType type;  // optional
    private Boolean completed;                   // optional
    private Integer streakCount;                 // optional

    public UpdateChallengeRequestDTO() {}

    public StreakChallenge.ChallengeType getType() { return type; }
    public void setType(StreakChallenge.ChallengeType type) { this.type = type; }

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

    public Integer getStreakCount() { return streakCount; }
    public void setStreakCount(Integer streakCount) { this.streakCount = streakCount; }
}
