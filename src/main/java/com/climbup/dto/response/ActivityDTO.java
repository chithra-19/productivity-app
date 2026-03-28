package com.climbup.dto.response;

import com.climbup.model.Activity;
import com.climbup.model.ActivityType;
import java.time.LocalDateTime;

public class ActivityDTO {

    private Long id;
    private String description;
    private ActivityType type;
    private LocalDateTime timestamp;
    private Integer focusMinutes;

    // Constructors
    public ActivityDTO() {}

    public ActivityDTO(Long id, String description, ActivityType type, LocalDateTime timestamp, Integer focusMinutes) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.timestamp = timestamp;
        this.focusMinutes = focusMinutes;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ActivityType getType() { return type; }
    public void setType(ActivityType type) { this.type = type; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Integer getFocusMinutes() { return focusMinutes; }
    public void setFocusMinutes(Integer focusMinutes) { this.focusMinutes = focusMinutes; }

    // Mapper from Entity
    public static ActivityDTO fromEntity(Activity activity) {
        return new ActivityDTO(
            activity.getId(),
            activity.getDescription(), // or getMessage() if your entity uses that
            activity.getType(),
            activity.getTimestamp(),
            activity.getFocusMinutes()
        );
    }
    
    public static ActivityDTO toDTO(Activity activity) {
        ActivityDTO dto = new ActivityDTO();
        dto.setDescription(activity.getDescription());
        dto.setTimestamp(activity.getTimestamp());
        return dto;
    }
}
