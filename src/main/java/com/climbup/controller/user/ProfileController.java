package com.climbup.controller.user;

import com.climbup.dto.request.ProfileRequestDTO;
import com.climbup.dto.response.ProfileResponseDTO;
import com.climbup.service.user.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // ---------- Create Profile ----------
    @PostMapping("/{userId}")
    public ResponseEntity<ProfileResponseDTO> createProfile(
            @PathVariable Long userId,
            @Valid @RequestBody ProfileRequestDTO profileRequestDTO) {

        ProfileResponseDTO response = profileService.updateProfile(userId, profileRequestDTO);
        return ResponseEntity.ok(response);
    }

    // ---------- Get Profile ----------
    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponseDTO> getProfile(@PathVariable Long userId) {
        ProfileResponseDTO response = profileService.getProfile(userId);
        return ResponseEntity.ok(response);
    }

    // ---------- Update Profile ----------
    @PutMapping("/{userId}")
    public ResponseEntity<ProfileResponseDTO> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody ProfileRequestDTO profileRequestDTO) {

        ProfileResponseDTO response = profileService.updateProfile(userId, profileRequestDTO);
        return ResponseEntity.ok(response);
    }
}
