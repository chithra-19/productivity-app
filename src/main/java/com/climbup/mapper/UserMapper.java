package com.climbup.mapper;

import com.climbup.dto.request.UserRequestDTO;
import com.climbup.dto.response.UserResponseDTO;
import com.climbup.model.User;

public class UserMapper {

    public static UserResponseDTO toResponseDTO(User user) {
        if (user == null) return null;

        String firstName = null;
        if (user.getProfile() != null) {
            firstName = user.getProfile().getFirstName();
        }

        return new UserResponseDTO(
            user.getId(),
            firstName,
            user.getEmail()
        );
    }

    public static User toEntity(UserRequestDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // hashed in UserService
        // No user.setFirstName() — name is stored in Profile
        return user;
    }
}