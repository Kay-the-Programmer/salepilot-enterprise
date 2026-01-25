package com.salepilot.backend.controller;

import com.salepilot.backend.dto.StockTakeResponse;
import com.salepilot.backend.entity.StockTake;
import com.salepilot.backend.entity.StockTakeItem;
import com.salepilot.backend.service.StockTakeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Stock Take management.
 */
@RestController
@RequestMapping("/api/v1/stock-takes")
@RequiredArgsConstructor
@Tag(name = "Stock Takes", description = "Inventory counting endpoints")
public class StockTakeController {

    private final StockTakeService stockTakeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Start new stock take session")
    public ResponseEntity<StockTakeResponse> startStockTake() {
        StockTake stockTake = stockTakeService.startStockTake();
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(stockTake));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get active stock take session")
    public ResponseEntity<StockTakeResponse> getActiveStockTake() {
        StockTake stockTake = stockTakeService.getActiveStockTake();
        return ResponseEntity.ok(mapToResponse(stockTake));
    }

    @PutMapping("/active/items/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Update counted quantity for item")
    public ResponseEntity<Void> updateItemCount(
            @PathVariable Long productId,
            @RequestBody Map<String, BigDecimal> request) {
        BigDecimal quantity = request.get("quantity");
        stockTakeService.updateItemCount(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/active/finalize")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Finalize stock take and apply adjustments")
    public ResponseEntity<StockTakeResponse> finalizeStockTake() {
        StockTake stockTake = stockTakeService.finalizeStockTake();
        return ResponseEntity.ok(mapToResponse(stockTake));
    }

    @GetMapping
    @Operation(summary = "List stock take history")
    public ResponseEntity<Page<StockTakeResponse>> getHistory(Pageable pageable) {
        Page<StockTake> history = stockTakeService.getStockTakeHistory(pageable);
        return ResponseEntity.ok(history.map(this::mapToResponse));
    }

    // Mapper helper
    private StockTakeResponse mapToResponse(StockTake stockTake) {
        List<StockTakeItem> items = stockTakeService.getStockTakeItems(stockTake.getId());

        List<StockTakeResponse.StockTakeItemResponse> itemResponses = items.stream()
                .map(item -> StockTakeResponse.StockTakeItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getName())
                        .sku(item.getSku())
                        .expected(item.getExpected())
                        .counted(item.getCounted())
                        .discrepancy(item.getDiscrepancy())
                        .hasDiscrepancy(item.hasDiscrepancy())
                        .build())
                .collect(Collectors.toList());

        return StockTakeResponse.builder()
                .id(stockTake.getId())
                .startTime(stockTake.getStartTime())
                .endTime(stockTake.getEndTime())
                .status(stockTake.getStatus().name())
                .items(itemResponses)
                .build();
    }
}
