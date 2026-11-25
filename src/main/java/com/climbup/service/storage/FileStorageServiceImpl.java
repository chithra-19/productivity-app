package com.climbup.service.storage;

import com.climbup.model.ProfilePicture;
import com.climbup.repository.ProfilePictureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final long MAX_BYTES = 2 * 1024 * 1024; // 2 MB

    // Allowed content types -> the extension used to check the real file bytes
    private static final Map<String, String> ALLOWED = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private final ProfilePictureRepository pictureRepository;

    public FileStorageServiceImpl(ProfilePictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    @Override
    @Transactional
    public String uploadProfileImage(MultipartFile file, Long userId) {

        if (file == null || file.isEmpty()) return null;

        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Picture must be 2 MB or smaller.");
        }

        String contentType = file.getContentType();
        String extension = ALLOWED.get(contentType);
        if (extension == null) {
            throw new IllegalArgumentException("Only JPG, PNG or WEBP pictures are allowed.");
        }

        try {
            byte[] bytes = file.getBytes();

            // Check the real file bytes, not just the type the browser claims
            if (!looksLikeImage(bytes, extension)) {
                throw new IllegalArgumentException("That file is not a valid picture.");
            }

            // One picture per user: update it if it exists, otherwise create it
            ProfilePicture picture = pictureRepository.findByUserId(userId)
                    .orElseGet(ProfilePicture::new);
            picture.setUserId(userId);
            picture.setContentType(contentType);
            picture.setData(bytes);
            picture.setUpdatedAt(Instant.now());
            pictureRepository.save(picture);

            // ?v=... makes the browser reload the new picture instead of showing the old one
            return "/profile/picture/" + userId + "?v=" + System.currentTimeMillis();

        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    private boolean looksLikeImage(byte[] h, String ext) {
        if (h.length < 12) return false;
        switch (ext) {
            case ".jpg":  return (h[0] & 0xFF) == 0xFF && (h[1] & 0xFF) == 0xD8;
            case ".png":  return (h[0] & 0xFF) == 0x89 && h[1] == 'P' && h[2] == 'N' && h[3] == 'G';
            case ".webp": return h[0] == 'R' && h[1] == 'I' && h[2] == 'F' && h[3] == 'F'
                              && h[8] == 'W' && h[9] == 'E' && h[10] == 'B' && h[11] == 'P';
            default:      return false;
        }
    }
}