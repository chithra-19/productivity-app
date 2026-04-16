package com.climbup.controller.user;

import com.climbup.dto.request.ProfileRequestDTO;
import com.climbup.dto.response.ProfileResponseDTO;
import com.climbup.model.User;
import com.climbup.service.user.ProfileService;
import com.climbup.service.user.UserService;
import com.climbup.service.storage.FileStorageService;

import jakarta.validation.Valid;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    public ProfileController(ProfileService profileService,
                             UserService userService,
                             FileStorageService fileStorageService) {
        this.profileService = profileService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    // =========================
    // GET PROFILE
    // =========================
    @GetMapping
    public ResponseEntity<ProfileResponseDTO> getMyProfile(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return ResponseEntity.ok(profileService.getProfile(user.getId()));
    }

    // =========================
    // UPDATE PROFILE (TEXT ONLY)
    // =========================
    @PutMapping
    public ResponseEntity<ProfileResponseDTO> updateProfile(
            Principal principal,
            @Valid @RequestBody ProfileRequestDTO dto) {

        User user = userService.findByEmail(principal.getName());

        return ResponseEntity.ok(
                profileService.updateProfile(user.getId(), dto)
        );
    }

    // =========================
    // UPLOAD PROFILE IMAGE ONLY
    // =========================
    @PostMapping("/picture")
    public ResponseEntity<ProfileResponseDTO> updateProfilePicture(
            Principal principal,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userService.findByEmail(principal.getName());

        String imageUrl = fileStorageService.uploadProfileImage(file);

        return ResponseEntity.ok(
                profileService.updateProfilePicture(user.getId(), imageUrl)
        );
    }
    
    
}