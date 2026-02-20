package com.salepilot.backend.service;

import com.salepilot.backend.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service for handling file uploads and storage.
 * Supports local filesystem and Firebase storage.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final AppProperties appProperties;

    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp");

    /**
     * Store a product image
     */
    public String storeProductImage(MultipartFile file, String storeId) {
        validateImageFile(file);
        return storeFile(file, storeId, "products");
    }

    /**
     * Store a verification document
     */
    public String storeVerificationDocument(MultipartFile file, String storeId) {
        validateDocumentFile(file);
        return storeFile(file, storeId, "verification");
    }

    /**
     * Store a user avatar
     */
    public String storeUserAvatar(MultipartFile file, String userId) {
        validateImageFile(file);
        return storeFile(file, userId, "avatars");
    }

    /**
     * Store a file to local filesystem
     */
    private String storeFile(MultipartFile file, String identifier, String category) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Cannot store empty file");
            }

            // Generate unique filename
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = getFileExtension(originalFilename);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String uniqueFilename = String.format("%s_%s_%s.%s",
                    category,
                    timestamp,
                    UUID.randomUUID().toString().substring(0, 8),
                    extension);

            // Create directory structure: uploads/{storeId}/{category}/
            Path uploadPath = Paths.get(appProperties.getStorage().getUploadDir())
                    .resolve(identifier)
                    .resolve(category);

            Files.createDirectories(uploadPath);

            // Copy file
            Path targetLocation = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Return relative path
            String relativePath = String.format("%s/%s/%s", identifier, category, uniqueFilename);

            log.info("File stored successfully: {}", relativePath);
            return relativePath;

        } catch (IOException ex) {
            log.error("Failed to store file", ex);
            throw new RuntimeException("Failed to store file: " + ex.getMessage());
        }
    }

    /**
     * Delete a file
     */
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(appProperties.getStorage().getUploadDir()).resolve(filePath);
            Files.deleteIfExists(path);
            log.info("File deleted: {}", filePath);
        } catch (IOException ex) {
            log.error("Failed to delete file: {}", filePath, ex);
            throw new RuntimeException("Failed to delete file: " + ex.getMessage());
        }
    }

    /**
     * Load file as resource
     */
    public Path loadFile(String filePath) {
        try {
            Path path = Paths.get(appProperties.getStorage().getUploadDir()).resolve(filePath);
            if (Files.exists(path) && Files.isReadable(path)) {
                return path;
            } else {
                throw new RuntimeException("File not found or not readable: " + filePath);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load file: " + ex.getMessage());
        }
    }

    /**
     * Validate image file
     */
    private void validateImageFile(MultipartFile file) {
        validateFile(file, ALLOWED_IMAGE_EXTENSIONS, "Invalid image file type");
    }

    /**
     * Validate document file
     */
    private void validateDocumentFile(MultipartFile file) {
        List<String> allowedExtensions = Arrays.asList(
                appProperties.getStorage().getAllowedExtensions().split(","));
        validateFile(file, allowedExtensions, "Invalid document file type");
    }

    /**
     * Validate file
     */
    private void validateFile(MultipartFile file, List<String> allowedExtensions, String errorMessage) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Check file size
        if (file.getSize() > appProperties.getStorage().getMaxFileSize()) {
            throw new IllegalArgumentException(
                    String.format("File size exceeds maximum allowed size of %d bytes",
                            appProperties.getStorage().getMaxFileSize()));
        }

        // Check file extension
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getFileExtension(filename).toLowerCase();

        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException(
                    errorMessage + ". Allowed: " + String.join(", ", allowedExtensions));
        }

        // Additional security: Check for path traversal
        if (filename.contains("..")) {
            throw new IllegalArgumentException("Filename contains invalid path sequence");
        }
    }

    /**
     * Get file extension
     */
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return filename.substring(lastDot + 1);
    }

    /**
     * Initialize storage directory
     */
    public void initializeStorage() {
        try {
            Path uploadPath = Paths.get(appProperties.getStorage().getUploadDir());
            Files.createDirectories(uploadPath);
            log.info("Storage directory initialized: {}", uploadPath.toAbsolutePath());
        } catch (IOException ex) {
            log.error("Failed to initialize storage directory", ex);
            throw new RuntimeException("Could not initialize storage!");
        }
    }
}
