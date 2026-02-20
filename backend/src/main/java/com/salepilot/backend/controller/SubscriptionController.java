package com.salepilot.backend.controller;

import com.salepilot.backend.entity.SubscriptionPlan;
import com.salepilot.backend.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Subscriptions.
 */
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Billing and Plan management")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/plans")
    @Operation(summary = "List available subscription plans")
    public ResponseEntity<List<SubscriptionPlan>> getPlans() {
        return ResponseEntity.ok(subscriptionService.getActivePlans());
    }

    @PostMapping("/subscribe")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Subscribe current store to a plan")
    public ResponseEntity<Void> subscribe(@RequestParam String planCode) {
        subscriptionService.subscribe(planCode);
        return ResponseEntity.ok().build();
    }
}
