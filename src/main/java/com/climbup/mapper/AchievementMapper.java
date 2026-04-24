package com.climbup.mapper;

import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.model.UserAchievement;

public class AchievementMapper {

    public static AchievementResponseDTO toResponseDTO(UserAchievement achievement) {
        if (achievement == null) return null;

        AchievementResponseDTO dto = new AchievementResponseDTO();
        dto.setId(achievement.getId());

        // 🔥 HANDLE CUSTOM GOAL ACHIEVEMENTS FIRST (has both template AND goal)
        if (achievement.getGoal() != null) {
            dto.setTitle(achievement.getDisplayTitle() != null
                    ? achievement.getDisplayTitle()
                    : achievement.getGoal().getTitle());
            dto.setDescription("Complete this goal to unlock!");
            dto.setType("GOAL");
            dto.setCategory("CUSTOM");
            dto.setIcon("bi-bullseye");
            dto.setRelatedGoalId(achievement.getGoal().getId());
        }
        // 🔥 HANDLE TEMPLATE ACHIEVEMENTS
        else if (achievement.getTemplate() != null) {
            dto.setTitle(achievement.getTemplate().getTitle());
            dto.setDescription(achievement.getTemplate().getDescription());
            dto.setType(
                    achievement.getTemplate().getType() != null
                            ? achievement.getTemplate().getType().name()
                            : "TEMPLATE"
            );
            dto.setCategory(achievement.getTemplate().getCategory());
            dto.setIcon(
                    achievement.getTemplate().getIcon() != null
                            ? achievement.getTemplate().getIcon()
                            : "bi-trophy"
            );
        }
        // 🔥 FALLBACK
        else {
            dto.setTitle("Unknown Achievement");
            dto.setDescription("No template or goal linked");
            dto.setType("UNKNOWN");
            dto.setCategory("SYSTEM");
            dto.setIcon("bi-question-circle");
        }

        // ✅ COMMON FIELDS
        dto.setUnlocked(achievement.isUnlocked());
        dto.setNewlyUnlocked(achievement.isNewlyUnlocked());
        dto.setSeen(achievement.isSeen());
        dto.setUnlockedDate(achievement.getUnlockedAt());
        dto.setCreatedAt(achievement.getCreatedAt());
        dto.setUserId(
                achievement.getUser() != null
                        ? achievement.getUser().getId()
                        : null
        );

        return dto;
    }
}