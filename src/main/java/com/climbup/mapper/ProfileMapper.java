package com.climbup.mapper;

import com.climbup.dto.request.ProfileRequestDTO;
import com.climbup.dto.response.ProfileResponseDTO;
import com.climbup.model.Profile;

public class ProfileMapper {

    // Convert ProfileRequestDTO → Profile entity
    public static Profile toEntity(ProfileRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Profile profile = new Profile();
        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setEmail(dto.getEmail());
        profile.setBio(dto.getBio());

        return profile;
    }

    // Convert Profile entity → ProfileResponseDTO
    public static ProfileResponseDTO toDTO(Profile profile) {
        if (profile == null) {
            return null;
        }

        ProfileResponseDTO dto = new ProfileResponseDTO();
        dto.setId(profile.getId());
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setEmail(profile.getEmail());
        dto.setBio(profile.getBio());
        dto.setUserId(profile.getUser() != null ? profile.getUser().getId() : null);

        return dto;
    }
}
