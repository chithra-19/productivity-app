package com.climbup.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO used for updating user profile details.
 */
public class ProfileUpdateDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must be at most 150 characters")
    private String email;

    @Size(max = 255, message = "Bio must be at most 255 characters")
    private String bio;

    // 🔹 Constructors
    public ProfileUpdateDTO() {}

    public ProfileUpdateDTO(String name, String email, String bio) {
        this.name = name;
        this.email = email;
        this.bio = bio;
    }

    // 🔹 Getters & Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
