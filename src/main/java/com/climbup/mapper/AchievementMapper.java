package com.climbup.mapper;

import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.model.Achievement;

public class AchievementMapper {

    // 🔹 Entity → ResponseDTO (PRIMARY use-case)
    public static AchievementResponseDTO toResponseDTO(Achievement achievement) {
        AchievementResponseDTO dto = new AchievementResponseDTO();

        dto.setId(achievement.getId());
        dto.setTitle(achievement.getTitle());
        dto.setDescription(achievement.getDescription());
        dto.setType(
                achievement.getType() != null
                        ? achievement.getType().name()
                        : null
        );
        dto.setCategory(achievement.getCategory());
        dto.setIcon(
                achievement.getIcon() != null
                        ? achievement.getIcon()
                        : "bi-trophy"
        );

        dto.setUnlocked(achievement.isUnlocked());
        dto.setNewlyUnlocked(achievement.isNewlyUnlocked());
        dto.setSeen(achievement.isSeen());

        dto.setUnlockedDate(achievement.getUnlockedDate());
        dto.setCreatedAt(achievement.getCreatedAt());

        dto.setUserId(
                achievement.getUser() != null
                        ? achievement.getUser().getId()
                        : null
        );

        dto.setProgressPercent(calculateProgressPercent(achievement));

        return dto;
    }

    // 🔹 Variant when caller already knows "newly unlocked"
    public static AchievementResponseDTO toResponseDTO(
            Achievement achievement,
            boolean newlyUnlocked
    ) {
        AchievementResponseDTO dto = toResponseDTO(achievement);
        dto.setNewlyUnlocked(newlyUnlocked);
        return dto;
    }

    // 🔹 Simple progress logic
    private static int calculateProgressPercent(Achievement achievement) {
        return achievement.isUnlocked() ? 100 : 0;
    }
}
