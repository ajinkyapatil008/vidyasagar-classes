package com.vidyasagar.backend.video;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${storage.local-path}")
    private String localPath;

    // Saves uploaded video to disk, returns a unique videoKey
    public String saveVideo(MultipartFile file) {
        try {
            Path storageDir = Paths.get(localPath);
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
            }

            // Generate unique filename to avoid collisions
            String extension = getExtension(file.getOriginalFilename());
            String videoKey = UUID.randomUUID() + extension;

            Path targetPath = storageDir.resolve(videoKey);
            Files.copy(file.getInputStream(), targetPath,
                    StandardCopyOption.REPLACE_EXISTING);

            return videoKey;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save video: " + e.getMessage());
        }
    }

    // Returns the actual file path for a given videoKey
    public Path getVideoPath(String videoKey) {
        Path path = Paths.get(localPath).resolve(videoKey);
        if (!Files.exists(path)) {
            throw new RuntimeException("Video file not found");
        }
        return path;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".mp4";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}