package com.salepilot.backend.controller;

import com.salepilot.backend.service.OnboardingService;
import com.salepilot.backend.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Onboarding and Verification.
 */
@RestController
@RequestMapping("/api/v1/setup")
@RequiredArgsConstructor
@Tag(name = "Setup & Verification", description = "Onboarding wizard and document verification")
public class SetupController {

    private final OnboardingService onboardingService;
    private final VerificationService verificationService;

    @GetMapping("/onboarding")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get current onboarding step")
    public ResponseEntity<Integer> getOnboardingStep() {
        return ResponseEntity.ok(onboardingService.getCurrentStep());
    }

    @PostMapping("/onboarding")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update onboarding step")
    public ResponseEntity<Void> setOnboardingStep(@RequestBody StepRequest request) {
        onboardingService.setStep(request.getStep());
        return ResponseEntity.ok().build();
    }

    // @PostMapping("/verification/documents")
    // @PreAuthorize("hasAnyRole('ADMIN')")
    // @Operation(summary = "Submit verification document")
    // public ResponseEntity<Void> submitDocument(@RequestBody DocumentRequest
    // request) {
    // // verificationService.submitDocument(request.getUrl(), request.getType());
    // return ResponseEntity.ok().build();
    // }

    @Data
    public static class StepRequest {
        private int step;
    }

    @Data
    public static class DocumentRequest {
        private String url;
        private String type;
    }
}
