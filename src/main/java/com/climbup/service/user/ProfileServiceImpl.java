package com.climbup.service.user;

import com.climbup.dto.request.ProfileRequestDTO;
import com.climbup.dto.response.ProfileResponseDTO;
import com.climbup.model.ActivityType;
import com.climbup.model.Profile;
import com.climbup.model.User;
import com.climbup.repository.ProfileRepository;
import com.climbup.repository.UserRepository;
import com.climbup.service.task.ActivityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ActivityService activityService;

    public ProfileServiceImpl(ProfileRepository profileRepository,
                              UserRepository userRepository,
                              ActivityService activityService) {

        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.activityService = activityService;
    }

    // =========================
    // CREATE PROFILE
    // =========================
    @Override
    public ProfileResponseDTO createProfile(Long userId, ProfileRequestDTO dto) {

        User user = getUser(userId);

        if (profileRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Profile already exists");
        }

        Profile profile = createNewProfile(user);

        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setBio(dto.getBio());
        profile.setProfilePictureUrl(dto.getProfilePictureUrl());

        profileRepository.save(profile);

        return buildProfileResponse(profile, user);
    }

    // =========================
    // GET PROFILE
    // =========================
    @Override
    public ProfileResponseDTO getProfile(Long userId) {

        User user = getUser(userId);
        Profile profile = getOrCreateProfile(user);

        return buildProfileResponse(profile, user);
    }

    // =========================
    // UPDATE PROFILE
    // =========================
    @Override
    public ProfileResponseDTO updateProfile(Long userId, ProfileRequestDTO dto) {

        User user = getUser(userId);
        Profile profile = getOrCreateProfile(user);

        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setBio(dto.getBio());

        if (dto.getProfilePictureUrl() != null) {
            profile.setProfilePictureUrl(dto.getProfilePictureUrl());
        }

        profileRepository.save(profile);

        activityService.log(
                "Profile updated",
                ActivityType.PROFILE_UPDATED,
                user
        );

        return buildProfileResponse(profile, user);
    }

    // =========================
    // UPDATE PROFILE PICTURE
    // =========================
    @Override
    public ProfileResponseDTO updateProfilePicture(Long userId, String imageUrl) {

        User user = getUser(userId);
        Profile profile = getOrCreateProfile(user);

        profile.setProfilePictureUrl(imageUrl);
        profileRepository.save(profile);

        activityService.log(
                "Profile picture updated",
                ActivityType.PROFILE_UPDATED,
                user
        );

        return buildProfileResponse(profile, user);
    }

    // =========================
    // HELPERS
    // =========================
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Profile getOrCreateProfile(User user) {
        return profileRepository.findByUser(user)
                .orElseGet(() -> createNewProfile(user));
    }

    public Profile findByUser(User user) {
        return profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    // =========================
    // CREATE NEW PROFILE
    // =========================
    private Profile createNewProfile(User user) {

        Profile profile = new Profile();

        profile.setUser(user);
        profile.setFirstName(user.getUsername());
        profile.setLastName(null);
        profile.setBio("No bio yet");
        profile.setProfilePictureUrl(null);

        profile.setLastActiveDate(LocalDate.now());

        profile.setAchievementList(new ArrayList<>());

        return profileRepository.save(profile);
    }

    // =========================
    // RESPONSE BUILDER (PURE PROFILE ONLY)
    // =========================
    private ProfileResponseDTO buildProfileResponse(Profile profile, User user) {

        ProfileResponseDTO dto = new ProfileResponseDTO();

        dto.setUserId(user.getId());
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setEmail(user.getEmail());
        dto.setBio(profile.getBio());
        dto.setProfilePictureUrl(profile.getProfilePictureUrl());
        dto.setLastActiveDate(profile.getLastActiveDate());

        dto.setAchievementList(
        	    profile.getAchievementList() != null
        	        ? new ArrayList<>(profile.getAchievementList())
        	        : new ArrayList<>()
        	);


        return dto;
    }
}