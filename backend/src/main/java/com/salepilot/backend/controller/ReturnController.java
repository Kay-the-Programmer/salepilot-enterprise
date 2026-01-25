package com.salepilot.backend.controller;

import com.salepilot.backend.dto.ReturnRequest;
import com.salepilot.backend.dto.ReturnResponse;
import com.salepilot.backend.entity.Return;
import com.salepilot.backend.entity.ReturnItem;
import com.salepilot.backend.service.ReturnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Returns & Refunds management.
 */
@RestController
@RequestMapping("/api/v1/returns")
@RequiredArgsConstructor
@Tag(name = "Returns", description = "Product return and refund endpoints")
public class ReturnController {

    private final ReturnService returnService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Create new return/refund")
    public ResponseEntity<ReturnResponse> createReturn(@Valid @RequestBody ReturnRequest request) {
        Return returnRecord = returnService.createReturn(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(returnRecord));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get return details")
    public ResponseEntity<ReturnResponse> getReturn(@PathVariable Long id) {
        Return returnRecord = returnService.getReturnById(id);
        return ResponseEntity.ok(mapToResponse(returnRecord));
    }

    @GetMapping
    @Operation(summary = "List all returns")
    public ResponseEntity<Page<ReturnResponse>> listReturns(Pageable pageable) {
        Page<Return> returns = returnService.getAllReturns(pageable);
        return ResponseEntity.ok(returns.map(this::mapToResponse));
    }

    // Mapper helper
    private ReturnResponse mapToResponse(Return returnRecord) {
        List<ReturnItem> items = returnService.getReturnItems(returnRecord.getId());

        List<ReturnResponse.ReturnItemResponse> itemResponses = items.stream()
                .map(item -> ReturnResponse.ReturnItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .reason(item.getReason())
                        .addToStock(item.getAddToStock())
                        .build())
                .collect(Collectors.toList());

        return ReturnResponse.builder()
                .id(returnRecord.getId())
                .returnId(returnRecord.getReturnId())
                .originalSaleId(returnRecord.getOriginalSale().getId())
                .originalTransactionId(returnRecord.getOriginalSale().getTransactionId())
                .timestamp(returnRecord.getTimestamp())
                .refundAmount(returnRecord.getRefundAmount())
                .refundMethod(returnRecord.getRefundMethod())
                .items(itemResponses)
                .build();
    }
}
