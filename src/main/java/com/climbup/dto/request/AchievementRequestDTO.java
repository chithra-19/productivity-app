package com.climbup.dto.request;

import com.climbup.model.Achievement.Type;
import com.climbup.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class AchievementRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @NotNull(message = "Type is required")
    private Type type;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @PastOrPresent(message = "Unlocked date cannot be in the future")
    private LocalDate unlockedDate;

    private User user; // <-- Added field

    public AchievementRequestDTO() {}

    public AchievementRequestDTO(String title, String description, Type type, String category, LocalDate unlockedDate, User user) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.category = category;
        this.unlockedDate = unlockedDate;
        this.user = user;
    }

    // Getters & Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getUnlockedDate() { return unlockedDate; }
    public void setUnlockedDate(LocalDate unlockedDate) { this.unlockedDate = unlockedDate; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
