package com.climbup.repository;

import com.climbup.model.ProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, Long> {
    Optional<ProfilePicture> findByUserId(Long userId);
}
