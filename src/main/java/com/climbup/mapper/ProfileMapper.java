package com.climbup.mapper;

import com.climbup.dto.response.ProfileResponseDTO;
import com.climbup.model.Profile;

public class ProfileMapper {

    public static ProfileResponseDTO toDTO(Profile profile) {
        if (profile == null) return null;

        ProfileResponseDTO dto = new ProfileResponseDTO();
        dto.setId(profile.getId());
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setEmail(profile.getEmail());
        dto.setBio(profile.getBio());
        dto.setProfilePictureUrl(profile.getProfilePictureUrl());
        dto.setStreak(profile.getStreak());
        dto.setCompletedTasks(profile.getCompletedTasks());
        dto.setProductivityScore(profile.getProductivityScore()); // ✅ int → int
        dto.setLastActiveDate(profile.getLastActiveDate());
        dto.setNewAchievement(profile.isNewAchievement());
        dto.setAchievementList(profile.getAchievementList());
        dto.setUserId(profile.getUser().getId());
        return dto;
    }

    public static Profile toEntity(ProfileResponseDTO dto) {
        if (dto == null) return null;

        Profile profile = new Profile();
        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setEmail(dto.getEmail());
        profile.setBio(dto.getBio());
        profile.setProfilePictureUrl(dto.getProfilePictureUrl());
        profile.setStreak(dto.getStreak());
        profile.setCompletedTasks(dto.getCompletedTasks());
        profile.setProductivityScore(dto.getProductivityScore()); // ✅ int → int
        profile.setLastActiveDate(dto.getLastActiveDate());
        profile.setNewAchievement(dto.isNewAchievement());
        profile.setAchievementList(dto.getAchievementList());
        // user must be set separately in service
        return profile;
    }
}