package com.climbup.mapper;

import java.time.ZoneOffset;

import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.model.Achievement;

public class AchievementMapper {

	public static AchievementResponseDTO toResponseDTO(Achievement achievement) {

	    if (achievement == null) return null;

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

	    // ✅ FIXED: no conversion needed
	    dto.setUnlockedDate(achievement.getUnlockedAt());

	    // ✅ FIXED: no conversion needed
	    dto.setCreatedAt(achievement.getCreatedAt());

	    dto.setUserId(
	        achievement.getUser() != null 
	            ? achievement.getUser().getId() 
	            : null
	    );

	    dto.setRelatedGoalId(
	        achievement.getGoal() != null 
	            ? achievement.getGoal().getId() 
	            : null
	    );

	    return dto;
	}

   
    // 🔹 Simple progress logic
    private static int calculateProgressPercent(Achievement achievement) {
        return achievement.isUnlocked() ? 100 : 0;
    }
}
