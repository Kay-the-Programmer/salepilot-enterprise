package com.salepilot.backend.controller;

import com.salepilot.backend.dto.SettingDTO;
import com.salepilot.backend.service.SettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Settings management.
 */
@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
@Tag(name = "Settings", description = "Store configuration endpoints")
public class SettingController {

    private final SettingService settingService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all store settings")
    public ResponseEntity<List<SettingDTO>> getAllSettings() {
        return ResponseEntity.ok(settingService.getAllSettings());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Update or create a setting")
    public ResponseEntity<SettingDTO> updateSetting(@Valid @RequestBody SettingDTO request) {
        return ResponseEntity.ok(settingService.updateSetting(request));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Bulk update settings")
    public ResponseEntity<List<SettingDTO>> updateSettings(@RequestBody List<SettingDTO> requests) {
        return ResponseEntity.ok(settingService.updateSettings(requests));
    }
}
