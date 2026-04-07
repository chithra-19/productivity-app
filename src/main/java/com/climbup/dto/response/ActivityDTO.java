package com.climbup.dto.response;

import com.climbup.model.Activity;
import com.climbup.model.ActivityType;

import java.time.Instant;

public class ActivityDTO {

    private Long id;
    private String description;
    private ActivityType type;
    private Instant timestamp;
    private Integer focusMinutes;

    public ActivityDTO() {}

    public ActivityDTO(Long id, String description, ActivityType type,
                       Instant timestamp, Integer focusMinutes) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.timestamp = timestamp;
        this.focusMinutes = focusMinutes;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public ActivityType getType() {
        return type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Integer getFocusMinutes() {
        return focusMinutes;
    }
    public void setType(ActivityType type) { this.type = type; }
    
    public void setFocusMinutes(Integer focusMinutes) {
        this.focusMinutes = focusMinutes;
    }
   
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setDescription(String description) { this.description = description; }

    public void setId(Long id) {
        this.id = id;
    }
    

    public static ActivityDTO fromEntity(Activity activity) {
        if (activity == null) return null;

        return new ActivityDTO(
            activity.getId(),
            activity.getDescription(),
            activity.getType(),
            activity.getTimestamp(), // MUST also be Instant in entity
            activity.getFocusMinutes()
        );
    }

	
}