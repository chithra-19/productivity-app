package com.climbup.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Clean DTO for updating user profile only.
 * Does NOT include authentication fields like email.
 */
public class ProfileUpdateDTO {

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    private String lastName;

    @Size(max = 255, message = "Bio must be at most 255 characters")
    private String bio;

    // Optional display-only field (NOT source of truth)
    private String profilePictureUrl;

    // ---------- Constructors ----------
    public ProfileUpdateDTO() {}

    public ProfileUpdateDTO(String firstName, String lastName, String bio) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
    }

    // ---------- Getters & Setters ----------
    public String getFirstName() {
        return firstName != null ? firstName.trim() : null;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName != null ? lastName.trim() : null;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}