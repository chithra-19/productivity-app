package com.climbup.controller.user;

import com.climbup.dto.request.ProfileRequestDTO;
import com.climbup.dto.response.ProfileResponseDTO;
import com.climbup.model.User;
import com.climbup.service.user.ProfileService;
import com.climbup.service.user.UserService;

import jakarta.validation.Valid;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;

    public ProfileController(ProfileService profileService,
                             UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    // ---------- Get Logged-in User Profile ----------
    @GetMapping
    public ResponseEntity<ProfileResponseDTO> getMyProfile(Principal principal) {

        User user = userService.findByEmail(principal.getName());

        ProfileResponseDTO response =
                profileService.getProfile(user.getId());

        return ResponseEntity.ok(response);
    }

    // ---------- Update Logged-in User Profile ----------
    @PutMapping
    public ResponseEntity<ProfileResponseDTO> updateMyProfile(
            Principal principal,
            @Valid @RequestBody ProfileRequestDTO profileRequestDTO) {

        User user = userService.findByEmail(principal.getName());

        ProfileResponseDTO response =
                profileService.updateProfile(user.getId(), profileRequestDTO);

        return ResponseEntity.ok(response);
    }
}
