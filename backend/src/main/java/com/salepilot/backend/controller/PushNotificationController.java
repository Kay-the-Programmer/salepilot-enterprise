package com.salepilot.backend.controller;

import com.salepilot.backend.dto.PushSubscriptionRequest;
import com.salepilot.backend.service.PushNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Push Notifications.
 */
@RestController
@RequestMapping("/api/v1/push")
@RequiredArgsConstructor
@Tag(name = "Push Notifications", description = "Web Push subscription endpoints")
public class PushNotificationController {

    private final PushNotificationService pushNotificationService;

    private Long getCurrentUserId() {
        return 1L; // Placeholder
    }

    @PostMapping("/subscribe")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Subscribe to push notifications")
    public ResponseEntity<Void> subscribe(@Valid @RequestBody PushSubscriptionRequest request) {
        pushNotificationService.subscribe(request, getCurrentUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unsubscribe")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Unsubscribe")
    public ResponseEntity<Void> unsubscribe(@RequestParam String endpoint) {
        pushNotificationService.unsubscribe(endpoint);
        return ResponseEntity.ok().build();
    }

    // Admin test endpoint
    @PostMapping("/test-send")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> testSend(@RequestParam Long userId, @RequestParam String message) {
        pushNotificationService.sendPush(userId, "Test Notification", message, "/");
        return ResponseEntity.ok().build();
    }
}
