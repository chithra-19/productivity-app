package com.climbup.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StreakTrackerRequestDTO {

    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category can be at most 100 characters")
    private String category; // e.g., "Coding", "Workout"

    // Optional: you can allow setting initial streak if needed
    // private Integer initialStreak;

    // 🔧 Getters and Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
