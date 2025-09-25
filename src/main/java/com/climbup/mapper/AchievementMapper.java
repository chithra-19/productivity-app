package com.climbup.mapper;

import com.climbup.dto.request.AchievementRequestDTO;
import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.model.Achievement;
import com.climbup.model.User;

public class AchievementMapper {

    // 🔹 RequestDTO → Entity
    public static Achievement toEntity(AchievementRequestDTO dto, User user) {
        Achievement achievement = new Achievement();
        achievement.setTitle(dto.getTitle());
        achievement.setDescription(dto.getDescription());
        achievement.setType(dto.getType());
        achievement.setCategory(dto.getCategory());
        achievement.setUnlockedDate(dto.getUnlockedDate());
        achievement.setUser(user);   // link to User
        return achievement;
    }

    // 🔹 Entity → ResponseDTO
    public static AchievementResponseDTO toResponseDTO(Achievement achievement) {
        AchievementResponseDTO dto = new AchievementResponseDTO();
        dto.setId(achievement.getId());
        dto.setTitle(achievement.getTitle());
        dto.setDescription(achievement.getDescription());
        dto.setType(achievement.getType() != null ? achievement.getType().name() : null);
        dto.setCategory(achievement.getCategory());
        dto.setIcon(achievement.getIcon());
        dto.setUnlocked(achievement.isUnlocked());
        dto.setNewlyUnlocked(achievement.isNewlyUnlocked());
        dto.setSeen(achievement.isSeen());
        dto.setUnlockedDate(achievement.getUnlockedDate());
        dto.setCreatedAt(achievement.getCreatedAt());
        dto.setUserId(achievement.getUser() != null ? achievement.getUser().getId() : null);

        // Optional: Calculate progressPercent here if needed
        dto.setProgressPercent(calculateProgressPercent(achievement));

        return dto;
    }

    // Example method to calculate progress (customize based on your logic)
    private static int calculateProgressPercent(Achievement achievement) {
        // For now, simple placeholder: 100 if unlocked, 0 if locked
        return achievement.isUnlocked() ? 100 : 0;
    }
}
