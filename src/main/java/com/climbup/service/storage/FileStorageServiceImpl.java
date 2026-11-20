package com.climbup.service.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final String UPLOAD_DIR = "uploads/profile-images/";
    private static final long MAX_BYTES = 2 * 1024 * 1024; // 2 MB

    // Allowed content types -> the extension WE choose (never trust the user's file name)
    private static final Map<String, String> ALLOWED = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    @Override
    public String uploadProfileImage(MultipartFile file) {

        if (file == null || file.isEmpty()) return null;

        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Picture must be 2 MB or smaller.");
        }

        String extension = ALLOWED.get(file.getContentType());
        if (extension == null) {
            throw new IllegalArgumentException("Only JPG, PNG or WEBP pictures are allowed.");
        }

        try {
            // Check the real file bytes, not just the type the browser claims
            if (!looksLikeImage(file, extension)) {
                throw new IllegalArgumentException("That file is not a valid picture.");
            }

            Path dir = Paths.get(UPLOAD_DIR);
            Files.createDirectories(dir);

            String filename = UUID.randomUUID() + extension;
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            }

            return "/" + UPLOAD_DIR + filename;

        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    private boolean looksLikeImage(MultipartFile file, String ext) throws IOException {
        byte[] h = new byte[12];
        try (InputStream in = file.getInputStream()) {
            if (in.read(h) < 12) return false;
        }
        switch (ext) {
            case ".jpg":  return (h[0] & 0xFF) == 0xFF && (h[1] & 0xFF) == 0xD8;
            case ".png":  return (h[0] & 0xFF) == 0x89 && h[1] == 'P' && h[2] == 'N' && h[3] == 'G';
            case ".webp": return h[0] == 'R' && h[1] == 'I' && h[2] == 'F' && h[3] == 'F'
                              && h[8] == 'W' && h[9] == 'E' && h[10] == 'B' && h[11] == 'P';
            default:      return false;
        }
    }
}