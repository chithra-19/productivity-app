package com.climbup.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.climbup.dto.request.ProfileRequestDTO;
import com.climbup.dto.response.ProfileResponseDTO;
import com.climbup.service.user.ProfileService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/dashboard/profile")
public class ProfileViewController {

    private final ProfileService profileService;

    public ProfileViewController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{userId}")
    public String showProfile(@PathVariable Long userId, Model model) {
        ProfileResponseDTO profile = profileService.getProfile(userId);
        ProfileRequestDTO dto = new ProfileRequestDTO();
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setEmail(profile.getEmail());
        dto.setBio(profile.getBio());
        dto.setProfilePictureUrl(profile.getProfilePictureUrl());

        model.addAttribute("profile", profile);
        model.addAttribute("profileRequestDTO", dto);
        return "profile"; // resolves to profile.html
    }

    @PostMapping("/update/{userId}")
    public String updateProfile(@PathVariable Long userId,
                                @Valid @ModelAttribute("profileRequestDTO") ProfileRequestDTO dto,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("profile", profileService.getProfile(userId));
            return "profile";
        }
        profileService.updateProfile(userId, dto);
        return "redirect:/dashboard/profile/" + userId;
    }
}