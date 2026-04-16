package com.climbup.dto.response;

import java.time.Instant;
import java.time.LocalDate;

import com.climbup.model.UserAchievement;


/**
 * DTO for sending achievement details in API responses.
 */
public class AchievementResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String type;       // Enum mapped as String
    private String category;
    private String icon;       // renamed for consistency with entity
    private boolean unlocked;
    private boolean newlyUnlocked;
    private boolean seen;
    private Instant unlockedDate;
    private Instant createdAt;
    private Long userId;
    private Long relatedGoalId;
   

   

	// Constructors
    public AchievementResponseDTO() {}

    public AchievementResponseDTO(Long id, String title, String description, String type,
                                  String category, String icon, boolean unlocked,
                                  boolean newlyUnlocked, boolean seen,
                                 Instant unlockedDate, Instant createdAt,
                                  Long userId, Long relatedGoalId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.category = category;
        this.icon = icon;
        this.unlocked = unlocked;
        this.newlyUnlocked = newlyUnlocked;
        this.seen = seen;
        this.unlockedDate = unlockedDate;
        this.createdAt = createdAt;
        this.userId = userId;
        this.relatedGoalId = relatedGoalId;
      
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }

    public boolean isNewlyUnlocked() { return newlyUnlocked; }
    public void setNewlyUnlocked(boolean newlyUnlocked) { this.newlyUnlocked = newlyUnlocked; }

    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }

    public Instant getUnlockedDate() { return unlockedDate; }
    public void setUnlockedDate(Instant unlockedDate) { this.unlockedDate = unlockedDate; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getRelatedGoalId() {
		return relatedGoalId;
	}

	public void setRelatedGoalId(Long relatedGoalId) {
		this.relatedGoalId = relatedGoalId;
	}
	
	public static AchievementResponseDTO fromEntity(UserAchievement ua) {
	    AchievementResponseDTO dto = new AchievementResponseDTO();

	    dto.setId(ua.getId());
	    dto.setTitle(
	        ua.getGoal() != null ? ua.getGoal().getTitle() :
	        ua.getTemplate() != null ? ua.getTemplate().getTitle() : null
	    );
	    dto.setDescription(
	        ua.getGoal() != null ? ua.getGoal().getDescription() :
	        ua.getTemplate() != null ? ua.getTemplate().getDescription() : null
	    );
	    dto.setType(
	        ua.getTemplate() != null && ua.getTemplate().getType() != null
	            ? ua.getTemplate().getType().name()
	            : null
	    );
	    dto.setCategory(
	        ua.getTemplate() != null ? ua.getTemplate().getCategory() : null
	    );
	    dto.setIcon(
	        ua.getTemplate() != null ? ua.getTemplate().getIcon() : "bi-trophy"
	    );

	    dto.setUnlocked(ua.isUnlocked());
	    dto.setNewlyUnlocked(ua.isNewlyUnlocked());
	    dto.setSeen(ua.isSeen());
	    dto.setUnlockedDate(ua.getUnlockedAt());
	    dto.setCreatedAt(ua.getCreatedAt());
	    dto.setUserId(ua.getUser() != null ? ua.getUser().getId() : null);
	    dto.setRelatedGoalId(ua.getGoal() != null ? ua.getGoal().getId() : null);

	    return dto;
	}


   }
