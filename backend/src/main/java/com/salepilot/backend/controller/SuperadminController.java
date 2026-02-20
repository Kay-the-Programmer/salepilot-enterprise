package com.salepilot.backend.controller;

import com.salepilot.backend.dto.AdminStoreDTO;
import com.salepilot.backend.entity.Store;
import com.salepilot.backend.service.SuperadminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Superadmin Features.
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Superadmin", description = "Platform administration endpoints")
public class SuperadminController {

    private final SuperadminService superadminService;

    @GetMapping("/stores")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "List all stores")
    public ResponseEntity<Page<AdminStoreDTO>> getAllStores(Pageable pageable) {
        return ResponseEntity.ok(superadminService.getAllStores(pageable));
    }

    @PatchMapping("/stores/{id}/status")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Update store status (Suspend/Activate)")
    public ResponseEntity<Void> updateStoreStatus(@PathVariable Long id, @RequestParam Store.StoreStatus status) {
        superadminService.updateStoreStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/stores/{id}/verify")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Manually verify store")
    public ResponseEntity<Void> verifyStore(@PathVariable Long id) {
        superadminService.verifyStore(id);
        return ResponseEntity.ok().build();
    }
}
