package com.climbup.mapper;

import com.climbup.dto.response.ProfileResponseDTO;
import com.climbup.model.Profile;

public class ProfileMapper {

    // =========================
    // ENTITY → DTO
    // =========================
    public static ProfileResponseDTO toDTO(Profile profile) {
        if (profile == null) return null;

        ProfileResponseDTO dto = new ProfileResponseDTO();

        dto.setUserId(
                profile.getUser() != null ? profile.getUser().getId() : null
        );

        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setBio(profile.getBio());
        dto.setProfilePictureUrl(profile.getProfilePictureUrl());

        dto.setCurrentStreak(profile.getStreak());
        dto.setCompletedTasks(profile.getCompletedTasks());
        dto.setProductivityScore(profile.getProductivityScore());

        dto.setLastActiveDate(profile.getLastActiveDate());
        
        dto.setNewAchievement(profile.isNewAchievement());
        dto.setAchievementList(profile.getAchievementList());

        return dto;
    }

    // =========================
    // REQUEST DTO → ENTITY (FIXED)
    // =========================
    public static Profile toEntity(com.climbup.dto.request.ProfileUpdateDTO dto) {
        if (dto == null) return null;

        Profile profile = new Profile();

        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setBio(dto.getBio());

        profile.setProfilePictureUrl(dto.getProfilePictureUrl());

        // ⚠️ IMPORTANT:
        // DO NOT set:
        // - email
        // - userId
        // These must be handled in service layer

        return profile;
    }
}