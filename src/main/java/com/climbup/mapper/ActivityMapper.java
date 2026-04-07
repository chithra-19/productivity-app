package com.climbup.mapper;

import com.climbup.dto.response.ActivityDTO;
import com.climbup.model.Activity;

import java.time.ZoneOffset;

public class ActivityMapper {

    // Convert Entity → DTO
    public static ActivityDTO toDTO(Activity activity) {
        if (activity == null) return null;

        ActivityDTO dto = new ActivityDTO();

        dto.setId(activity.getId());
        dto.setDescription(activity.getDescription());
        dto.setType(activity.getType());
        dto.setFocusMinutes(activity.getFocusMinutes());

        // ✅ safe timestamp mapping
        dto.setTimestamp(activity.getTimestamp());

        return dto;
    }

    // Convert DTO → Entity
    public static Activity toEntity(ActivityDTO dto) {
        if (dto == null) return null;

        Activity activity = new Activity();

        activity.setId(dto.getId()); // optional (usually not needed for new entity)
        activity.setDescription(dto.getDescription());
        activity.setType(dto.getType());
        activity.setFocusMinutes(dto.getFocusMinutes());

        // ❗ Only set if your entity supports it
        activity.setTimestamp(dto.getTimestamp());

        return activity;
    }
}