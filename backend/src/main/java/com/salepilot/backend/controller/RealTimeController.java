package com.salepilot.backend.controller;

import com.salepilot.backend.service.RealTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to test Real-Time features manually.
 */
@RestController
@RequestMapping("/api/v1/realtime")
@RequiredArgsConstructor
@Tag(name = "Real-Time", description = "WebSocket test endpoints")
public class RealTimeController {

    private final RealTimeService realTimeService;

    @PostMapping("/test-sale")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Simulate new sale event")
    public ResponseEntity<Void> testSaleEvent(@RequestParam String storeId,
            @RequestParam Long saleId,
            @RequestParam java.math.BigDecimal amount) {
        realTimeService.notifyNewSale(storeId, saleId, amount);
        return ResponseEntity.ok().build();
    }
}
