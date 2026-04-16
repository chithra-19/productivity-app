package com.climbup.service.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final String uploadDir = "uploads/profile-images/";

    @Override
    public String uploadProfileImage(MultipartFile file) {

        try {
            if (file == null || file.isEmpty()) return null;

            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            Path path = Paths.get(uploadDir + filename);
            Files.copy(file.getInputStream(), path);

            return "/" + uploadDir + filename;

        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}