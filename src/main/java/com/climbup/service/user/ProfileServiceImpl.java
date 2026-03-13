package com.climbup.service.user;

import com.climbup.dto.request.ProfileRequestDTO;
import com.climbup.dto.response.ProfileResponseDTO;
import com.climbup.model.Profile;
import com.climbup.model.User;
import com.climbup.repository.ProfileRepository;
import com.climbup.repository.UserRepository;
import com.climbup.repository.TaskRepository;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.productivity.XPService;

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
    private final TaskRepository taskRepository;
    private final StreakTrackerService streakTrackerService;
    private final XPService xpService;

    public ProfileServiceImpl(ProfileRepository profileRepository,
                              UserRepository userRepository,
                              TaskRepository taskRepository,
                              StreakTrackerService streakTrackerService,
                              XPService xpService) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.streakTrackerService = streakTrackerService;
        this.xpService = xpService;
    }

    // ---------- Create Profile ----------
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

        // Initialize profile (static info only)
        profile.setLastActiveDate(LocalDate.now());
        profile.setNewAchievement(false);
        profile.setAchievementList(new ArrayList<>());

        return toDTO(profileRepository.save(profile), user);
    }

    // ---------- Get Profile ----------
    @Override
    public ProfileResponseDTO getProfile(Long userId) {
        // --- Fetch user and profile (static info) ---
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = getOrCreateProfile(user);

        // --- Aggregate dynamic stats ---
        long completedTasks = taskRepository.countByUserAndCompletedTrue(user);
        int currentStreak = streakTrackerService.getCurrentStreak(user);
        int bestStreak = streakTrackerService.getBestStreak(user.getId());

        long xp = xpService.getCurrentXP(user.getId());
        int level = xpService.getLevel(user.getId());
        int levelProgress = xpService.getProgressToNextLevel(xp); // 0-100%

        // --- Build DTO ---
        ProfileResponseDTO dto = toDTO(profile, user);
        dto.setCompletedTasks(completedTasks);
        dto.setCurrentStreak(currentStreak);
        dto.setBestStreak(bestStreak);
        dto.setXp(xp);
        dto.setLevel(level);
        dto.setLevelProgress(levelProgress);

        return dto;
    }


    // ---------- Update Profile ----------
    @Override
    public ProfileResponseDTO updateProfile(Long userId, ProfileRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = getOrCreateProfile(user); // static info only

        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setEmail(dto.getEmail());
        profile.setBio(dto.getBio());
        profile.setProfilePictureUrl(dto.getProfilePictureUrl());

        profileRepository.save(profile);

        // Return fully aggregated data
        return getProfile(userId);
    }

    // ---------- Helper Methods ----------
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
            profile.setLastActiveDate(LocalDate.now());
            profile.setNewAchievement(false);
            profile.setAchievementList(new ArrayList<>());
            return profileRepository.save(profile);
        });
    }

    // ---------- Mapper ----------
    private ProfileResponseDTO toDTO(Profile profile, User user) {
        ProfileResponseDTO dto = new ProfileResponseDTO();
        dto.setId(profile.getId());
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setEmail(profile.getEmail());
        dto.setBio(profile.getBio());
        dto.setUserId(user.getId());

        // Static / placeholder stats (dynamic stats set separately)
        dto.setCurrentStreak(0);
        dto.setCompletedTasks(0);
        dto.setProductivityScore(0);
        dto.setLastActiveDate(profile.getLastActiveDate());
        dto.setNewAchievement(profile.isNewAchievement());
        dto.setAchievementList(profile.getAchievementList() != null ? profile.getAchievementList() : new ArrayList<>());

        return dto;
    }

    // ---------- Profile Image Upload ----------
    @Override
    public String saveProfileImage(MultipartFile file) {
        if (file.isEmpty()) return null;

        try {
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String uploadDir = "uploads/profile-images/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            Path filePath = Paths.get(uploadDir + filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/" + uploadDir + filename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
