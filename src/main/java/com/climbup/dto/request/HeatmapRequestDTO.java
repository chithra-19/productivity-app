package com.climbup.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class HeatmapRequestDTO {
    
    @NotNull(message = "User ID is required")
    private Long userId;

    private String category = "all";  // default = all activities

    @Min(value = 1, message = "Days must be at least 1")
    private int days = 30;

    // Getters & setters
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public int getDays() {
        return days;
    }
    public void setDays(int days) {
        this.days = days;
    }
}
