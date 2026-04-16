package com.climbup.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public class AchievementRequestDTO {

    @NotNull(message = "Template code is required")
    private String templateCode;   // ✅ IMPORTANT

    @PastOrPresent(message = "Unlocked date cannot be in the future")
    private LocalDate unlockedDate;

    public AchievementRequestDTO() {}

    public AchievementRequestDTO(String templateCode, LocalDate unlockedDate) {
        this.templateCode = templateCode;
        this.unlockedDate = unlockedDate;
    }

    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }

    public LocalDate getUnlockedDate() { return unlockedDate; }
    public void setUnlockedDate(LocalDate unlockedDate) { this.unlockedDate = unlockedDate; }
}