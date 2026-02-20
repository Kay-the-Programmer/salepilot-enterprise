package com.salepilot.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salepilot.backend.entity.Store;
import com.salepilot.backend.entity.User;
import com.salepilot.backend.repository.StoreRepository;
import com.salepilot.backend.repository.UserRepository;
import com.salepilot.backend.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/verification")
@RequiredArgsConstructor
@Tag(name = "Verification", description = "Store verification and document management")
public class VerificationController {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload verification document")
    public ResponseEntity<VerificationDocument> uploadDocument(
            @RequestParam("document") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (user.getCurrentStoreId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No store context selected");
        }

        Long storeId;
        try {
            storeId = Long.parseLong(user.getCurrentStoreId());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid store ID format");
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }

        try {
            // Upload file
            String fileUrl = storageService.storeVerificationDocument(file, store.getId().toString());

            // Create document object
            VerificationDocument doc = VerificationDocument.builder()
                    .id("doc_" + UUID.randomUUID().toString())
                    .name(file.getOriginalFilename())
                    .url(fileUrl)
                    .uploadedAt(LocalDateTime.now().toString())
                    .build();

            // Update store
            List<VerificationDocument> docs = new ArrayList<>();
            if (store.getVerificationDocuments() != null && !store.getVerificationDocuments().isEmpty()) {
                try {
                    docs = new ArrayList<>(List.of(
                            objectMapper.readValue(store.getVerificationDocuments(), VerificationDocument[].class)));
                } catch (Exception e) {
                    // Start fresh if parsing fails
                }
            }
            docs.add(doc);
            store.setVerificationDocuments(objectMapper.writeValueAsString(docs));
            storeRepository.save(store);

            return new ResponseEntity<>(doc, HttpStatus.CREATED);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload file", e);
        }
    }

    @GetMapping("/status")
    @Operation(summary = "Get verification status")
    public ResponseEntity<Map<String, Object>> getStatus(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (user.getCurrentStoreId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No store context selected");
        }

        Long storeId;
        try {
            storeId = Long.parseLong(user.getCurrentStoreId());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid store ID format");
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        List<VerificationDocument> docs = new ArrayList<>();
        if (store.getVerificationDocuments() != null && !store.getVerificationDocuments().isEmpty()) {
            try {
                docs = List.of(objectMapper.readValue(store.getVerificationDocuments(), VerificationDocument[].class));
            } catch (Exception e) {
                // Return empty list on parse error
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("isVerified", store.getIsVerified());
        response.put("verificationDocuments", docs);

        return ResponseEntity.ok(response);
    }

    @Data
    @Builder
    public static class VerificationDocument {
        private String id;
        private String name;
        private String url;
        private String uploadedAt;
    }
}
