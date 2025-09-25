package com.climbup.mapper;

import com.climbup.dto.request.StreakChallengeRequestDTO;
import com.climbup.dto.response.StreakChallengeResponseDTO;
import com.climbup.model.StreakChallenge;
import com.climbup.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class StreakChallengeMapper {

    // ---------------- Entity → Response DTO ----------------
    public static StreakChallengeResponseDTO toResponseDTO(StreakChallenge challenge) {
        if (challenge == null) return null;

        return new StreakChallengeResponseDTO(
                challenge.getId(),
                challenge.getDurationDays(),
                challenge.getCurrentStreak(),
                challenge.isCompleted(),
                challenge.getStartDate(),
                challenge.getLastCompletedDate()
        );
    }

    // ---------------- Request DTO → Entity ----------------
    public static StreakChallenge toEntity(StreakChallengeRequestDTO dto, User user) {
        if (dto == null) throw new IllegalArgumentException("StreakChallengeRequestDTO cannot be null");
        if (user == null) throw new IllegalArgumentException("User cannot be null");

        // Use the existing constructor
        return new StreakChallenge(dto.getType(), user);
    }

    // ---------------- List<Entity> → List<ResponseDTO> ----------------
    public static List<StreakChallengeResponseDTO> toResponseDTOList(List<StreakChallenge> challenges) {
        if (challenges == null) return List.of();
        return challenges.stream()
                .map(StreakChallengeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
