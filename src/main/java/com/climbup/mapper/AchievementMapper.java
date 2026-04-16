package com.climbup.mapper;

import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.model.UserAchievement;

public class AchievementMapper {

    public static AchievementResponseDTO toResponseDTO(UserAchievement achievement) {

        if (achievement == null) return null;

        AchievementResponseDTO dto = new AchievementResponseDTO();

        dto.setId(achievement.getId());

        // ✅ FROM TEMPLATE
        dto.setTitle(achievement.getTemplate().getTitle());
        dto.setDescription(achievement.getTemplate().getDescription());
        dto.setType(
                achievement.getTemplate().getType() != null
                        ? achievement.getTemplate().getType().name()
                        : null
        );
        dto.setCategory(achievement.getTemplate().getCategory());
        dto.setIcon(
                achievement.getTemplate().getIcon() != null
                        ? achievement.getTemplate().getIcon()
                        : "bi-trophy"
        );

        // ✅ FROM USER ACHIEVEMENT
        dto.setUnlocked(achievement.isUnlocked());
        dto.setUnlockedDate(achievement.getUnlockedAt());
        dto.setCreatedAt(achievement.getCreatedAt());

        dto.setUserId(
                achievement.getUser() != null
                        ? achievement.getUser().getId()
                        : null
        );

        // ❌ REMOVE this (you don’t have goal anymore)
        // dto.setRelatedGoalId(...);

        return dto;
    }

    private static int calculateProgressPercent(UserAchievement achievement) {
        return achievement.isUnlocked() ? 100 : 0;
    }
}