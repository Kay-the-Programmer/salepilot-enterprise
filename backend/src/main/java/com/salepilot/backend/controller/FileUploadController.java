package com.salepilot.backend.controller;

import com.salepilot.backend.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for file upload and download operations
 */
@Slf4j
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final StorageService storageService;

    /**
     * Upload product image
     */
    @PostMapping("/product-image")
    public ResponseEntity<Map<String, String>> uploadProductImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("storeId") String storeId) {

        log.info("Uploading product image for store: {}", storeId);

        String filePath = storageService.storeProductImage(file, storeId);
        String fileUrl = "/uploads/" + filePath;

        Map<String, String> response = new HashMap<>();
        response.put("url", fileUrl);
        response.put("path", filePath);
        response.put("message", "File uploaded successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Upload verification document
     */
    @PostMapping("/verification-document")
    public ResponseEntity<Map<String, String>> uploadVerificationDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("storeId") String storeId) {

        log.info("Uploading verification document for store: {}", storeId);

        String filePath = storageService.storeVerificationDocument(file, storeId);
        String fileUrl = "/uploads/" + filePath;

        Map<String, String> response = new HashMap<>();
        response.put("url", fileUrl);
        response.put("path", filePath);
        response.put("message", "Document uploaded successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Upload user avatar
     */
    @PostMapping("/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {

        log.info("Uploading avatar for user: {}", userDetails.getUsername());

        String filePath = storageService.storeUserAvatar(file, userDetails.getUsername());
        String fileUrl = "/uploads/" + filePath;

        Map<String, String> response = new HashMap<>();
        response.put("url", fileUrl);
        response.put("path", filePath);
        response.put("message", "Avatar uploaded successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Serve uploaded files
     */
    @GetMapping("/{identifier}/{category}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(
            @PathVariable String identifier,
            @PathVariable String category,
            @PathVariable String filename) {

        String filePath = String.format("%s/%s/%s", identifier, category, filename);
        log.debug("Serving file: {}", filePath);

        Path path = storageService.loadFile(filePath);
        Resource resource = new FileSystemResource(path);

        String contentType = determineContentType(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    /**
     * Delete uploaded file
     */
    @DeleteMapping("/{identifier}/{category}/{filename:.+}")
    public ResponseEntity<Map<String, String>> deleteFile(
            @PathVariable String identifier,
            @PathVariable String category,
            @PathVariable String filename) {

        String filePath = String.format("%s/%s/%s", identifier, category, filename);
        log.info("Deleting file: {}", filePath);

        storageService.deleteFile(filePath);

        Map<String, String> response = new HashMap<>();
        response.put("message", "File deleted successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Determine content type from filename
     */
    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default -> "application/octet-stream";
        };
    }
}
