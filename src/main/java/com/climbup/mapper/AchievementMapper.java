package com.climbup.mapper;

import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.model.UserAchievement;

public class AchievementMapper {

    public static AchievementResponseDTO toResponseDTO(UserAchievement achievement) {

        if (achievement == null) return null;

        AchievementResponseDTO dto = new AchievementResponseDTO();

        dto.setId(achievement.getId());

        // 🔥 HANDLE TEMPLATE ACHIEVEMENTS
        if (achievement.getTemplate() != null) {

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
        // 🔥 HANDLE CUSTOM GOAL ACHIEVEMENTS
        else if (achievement.getGoal() != null) {

            dto.setTitle(achievement.getGoal().getTitle());
            dto.setDescription(achievement.getGoal().getDescription());

            dto.setType("GOAL");
            dto.setCategory("CUSTOM");
            dto.setIcon("bi-bullseye");

            // 🔗 important for frontend linking
            dto.setRelatedGoalId(achievement.getGoal().getId());
        }
        // 🔥 FINAL FALLBACK (prevents crashes forever)
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