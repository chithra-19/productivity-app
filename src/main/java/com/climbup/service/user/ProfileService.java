package com.climbup.service.user;

import com.climbup.dto.request.ProfileRequestDTO;
import com.climbup.dto.response.ProfileResponseDTO;

public interface ProfileService {

    ProfileResponseDTO createProfile(Long userId, ProfileRequestDTO dto);

    ProfileResponseDTO getProfile(Long userId);

    ProfileResponseDTO updateProfile(Long userId, ProfileRequestDTO dto);
    
    ProfileResponseDTO updateProfilePicture(Long userId, String imageUrl);

}