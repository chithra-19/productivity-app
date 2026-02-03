package com.climbup.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDateTime unlockedDate;
    private LocalDateTime createdAt;
    private Long userId;
    private int progressPercent;

    // Constructors
    public AchievementResponseDTO() {}

    public AchievementResponseDTO(Long id, String title, String description, String type,
                                  String category, String icon, boolean unlocked,
                                  boolean newlyUnlocked, boolean seen,
                                  LocalDateTime unlockedDate, LocalDateTime createdAt,
                                  Long userId, int progressPercent) {
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
        this.progressPercent = progressPercent;
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

    public LocalDateTime getUnlockedDate() { return unlockedDate; }
    public void setUnlockedDate(LocalDateTime unlockedDate) { this.unlockedDate = unlockedDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }
}
