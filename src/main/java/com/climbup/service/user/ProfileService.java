package com.climbup.service.user;

import com.climbup.dto.request.ProfileRequestDTO;
import com.climbup.dto.response.ProfileResponseDTO;

/**
 * Service interface for Profile operations.
 */
public interface ProfileService {

    /**
     * Create a new profile for a given user.
     *
     * @param userId The ID of the user.
     * @param profileRequestDTO The profile data to create.
     * @return The created profile as ProfileResponseDTO.
     */
    ProfileResponseDTO createProfile(Long userId, ProfileRequestDTO profileRequestDTO);

    /**
     * Get the profile of a given user.
     *
     * @param userId The ID of the user.
     * @return The profile as ProfileResponseDTO.
     */
    ProfileResponseDTO getProfile(Long userId);

    /**
     * Update the profile of a given user.
     *
     * @param userId The ID of the user.
     * @param profileRequestDTO The updated profile data.
     * @return The updated profile as ProfileResponseDTO.
     */
    ProfileResponseDTO updateProfile(Long userId, ProfileRequestDTO profileRequestDTO);
}
