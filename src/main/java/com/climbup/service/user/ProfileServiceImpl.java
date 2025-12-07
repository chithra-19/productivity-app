package com.climbup.service.user;

import com.climbup.dto.request.ProfileRequestDTO;
import com.climbup.dto.response.ProfileResponseDTO;
import com.climbup.model.Profile;
import com.climbup.model.User;
import com.climbup.repository.ProfileRepository;
import com.climbup.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ProfileResponseDTO createProfile(Long userId, ProfileRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (profileRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Profile already exists for this user");
        }

        Profile profile = new Profile();
        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setEmail(dto.getEmail());
        profile.setBio(dto.getBio());
        profile.setUser(user);

        // Initialize stats
        profile.setStreak(0);
        profile.setCompletedTasks(0);
        profile.setProductivityScore(0);
        profile.setLastActiveDate(LocalDate.now());
        profile.setNewAchievement(false);
        profile.setAchievementList(new ArrayList<>());

        return toDTO(profileRepository.save(profile));
    }

    @Override
    public ProfileResponseDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Use getOrCreateProfile instead of strict find
        Profile profile = getOrCreateProfile(user);

        return toDTO(profile);
    }


    @Override
    public ProfileResponseDTO updateProfile(Long userId, ProfileRequestDTO dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = getOrCreateProfile(user); // <-- safe now

        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setEmail(dto.getEmail());
        profile.setBio(dto.getBio());
        profile.setProfilePictureUrl(dto.getProfilePictureUrl());

        profileRepository.save(profile);

        return toDTO(profile);
    }


    @Override
    public Profile findByUser(User user) {
        return profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    @Override
    public Profile getOrCreateProfile(User user) {
        return profileRepository.findByUser(user).orElseGet(() -> {
            Profile profile = new Profile();
            profile.setUser(user);
            profile.setFirstName(user.getUsername());
            profile.setEmail(user.getEmail());
            profile.setBio("No bio yet.");
            profile.setProfilePictureUrl(null);
            profile.setStreak(0);
            profile.setCompletedTasks(0);
            profile.setProductivityScore(0);
            profile.setLastActiveDate(LocalDate.now());
            profile.setNewAchievement(false);
            profile.setAchievementList(new ArrayList<>());
            return profileRepository.save(profile);
        });
    }

    // Mapper
    private ProfileResponseDTO toDTO(Profile profile) {
        ProfileResponseDTO dto = new ProfileResponseDTO();
        dto.setId(profile.getId());
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setEmail(profile.getEmail());
        dto.setBio(profile.getBio());
        dto.setUserId(profile.getUser().getId());

        dto.setStreak(profile.getStreak());
        dto.setCompletedTasks(profile.getCompletedTasks());
        dto.setProductivityScore(profile.getProductivityScore());
        dto.setLastActiveDate(profile.getLastActiveDate());
        dto.setNewAchievement(profile.isNewAchievement());
        dto.setAchievementList(profile.getAchievementList() != null ? profile.getAchievementList() : new ArrayList<>());

        return dto;
    }
    
    @Override
    public String saveProfileImage(MultipartFile file) {
        if (file.isEmpty()) return null;

        try {
            // Generate a unique filename
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // Folder to store images (create it if it doesn't exist)
            String uploadDir = "uploads/profile-images/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // Save the file locally
            Path filePath = Paths.get(uploadDir + filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return the relative or absolute URL/path to store in DB
            return "/" + uploadDir + filename; // Example: "/uploads/profile-images/12345_pic.png"

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}