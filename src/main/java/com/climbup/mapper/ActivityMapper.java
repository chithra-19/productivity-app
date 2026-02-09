package com.climbup.mapper;

import com.climbup.dto.response.ActivityDTO;
import com.climbup.model.Activity;

public class ActivityMapper {

    // Convert Activity entity to DTO
    public static ActivityDTO toDTO(Activity activity) {
        if (activity == null) return null;

        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setDescription(activity.getDescription()); // or getMessage() if your entity uses that
        dto.setType(activity.getType());
        dto.setTimestamp(activity.getTimestamp());
        dto.setFocusMinutes(activity.getFocusMinutes());

        return dto;
    }

    // Optionally, convert DTO back to entity
    public static Activity toEntity(ActivityDTO dto) {
        if (dto == null) return null;

        Activity activity = new Activity();
        activity.setDescription(dto.getDescription());
        activity.setType(dto.getType());
        activity.setFocusMinutes(dto.getFocusMinutes());
        // timestamp and id are usually managed by JPA
        return activity;
    }
}
  