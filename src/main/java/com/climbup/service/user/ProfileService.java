package com.climbup.service.user;

import org.springframework.web.multipart.MultipartFile;

import com.climbup.dto.request.ProfileRequestDTO;
import com.climbup.dto.response.ProfileResponseDTO;
import com.climbup.model.Profile;
import com.climbup.model.User;

public interface ProfileService {
    ProfileResponseDTO createProfile(Long userId, ProfileRequestDTO profileRequestDTO);
    ProfileResponseDTO getProfile(Long userId);
    ProfileResponseDTO updateProfile(Long userId, ProfileRequestDTO profileRequestDTO);
    Profile getOrCreateProfile(User user);
    Profile findByUser(User user);
	String saveProfileImage(MultipartFile file);
}