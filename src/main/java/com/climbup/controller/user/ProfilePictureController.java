package com.climbup.controller.user;

import com.climbup.model.ProfilePicture;
import com.climbup.repository.ProfilePictureRepository;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Duration;

@Controller
public class ProfilePictureController {

    private final ProfilePictureRepository pictureRepository;

    public ProfilePictureController(ProfilePictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    @GetMapping("/profile/picture/{userId}")
    public ResponseEntity<byte[]> picture(@PathVariable Long userId) {
        return pictureRepository.findByUserId(userId)
                .map((ProfilePicture p) -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(p.getContentType()))
                        .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePrivate())
                        .body(p.getData()))
                .orElse(ResponseEntity.notFound().build());
    }
}