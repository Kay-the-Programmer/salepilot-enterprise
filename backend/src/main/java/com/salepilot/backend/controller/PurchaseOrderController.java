package com.salepilot.backend.controller;

import com.salepilot.backend.dto.PurchaseOrderRequest;
import com.salepilot.backend.dto.PurchaseOrderResponse;
import com.salepilot.backend.entity.PurchaseOrder;
import com.salepilot.backend.entity.PurchaseOrderItem;
import com.salepilot.backend.service.PurchaseOrderService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Purchase Order management.
 */
@RestController
@RequestMapping("/api/v1/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Purchase Orders", description = "Supplier order and receiving endpoints")
public class PurchaseOrderController {

    private final PurchaseOrderService poService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Create new purchase order (Draft)")
    public ResponseEntity<PurchaseOrderResponse> createPO(@Valid @RequestBody PurchaseOrderRequest request) {
        PurchaseOrder po = poService.createPO(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(po));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get purchase order details")
    public ResponseEntity<PurchaseOrderResponse> getPO(@PathVariable Long id) {
        PurchaseOrder po = poService.getPOById(id);
        return ResponseEntity.ok(mapToResponse(po));
    }

    @GetMapping
    @Operation(summary = "List purchase orders")
    public ResponseEntity<Page<PurchaseOrderResponse>> listPOs(Pageable pageable) {
        Page<PurchaseOrder> pos = poService.getAllPOs(pageable);
        return ResponseEntity.ok(pos.map(this::mapToResponse));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Update PO status (e.g. DRAFT -> ORDERED)")
    public ResponseEntity<PurchaseOrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusMap) {
        String statusStr = statusMap.get("status");
        PurchaseOrder.POStatus status = PurchaseOrder.POStatus.valueOf(statusStr);
        PurchaseOrder po = poService.updateStatus(id, status);
        return ResponseEntity.ok(mapToResponse(po));
    }

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Receive inventory from PO")
    public ResponseEntity<PurchaseOrderResponse> receiveInventory(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> receivedItems) {
        // Map keys are Strings (productId) from JSON, need to parse to Long
        Map<Long, BigDecimal> items = receivedItems.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> Long.parseLong(e.getKey()),
                        Map.Entry::getValue));

        PurchaseOrder po = poService.receiveInventory(id, items);
        return ResponseEntity.ok(mapToResponse(po));
    }

    // Mapper helper
    private PurchaseOrderResponse mapToResponse(PurchaseOrder po) {
        List<PurchaseOrderItem> items = poService.getPOItems(po.getId());

        List<PurchaseOrderResponse.PurchaseOrderItemResponse> itemResponses = items.stream()
                .map(item -> PurchaseOrderResponse.PurchaseOrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProductName())
                        .sku(item.getSku())
                        .quantity(item.getQuantity())
                        .costPrice(item.getCostPrice())
                        .receivedQuantity(item.getReceivedQuantity())
                        .total(item.getLineTotal())
                        .fullyReceived(item.isFullyReceived())
                        .build())
                .collect(Collectors.toList());

        return PurchaseOrderResponse.builder()
                .id(po.getId())
                .poNumber(po.getPoNumber())
                .supplierId(po.getSupplier().getId())
                .supplierName(po.getSupplierName())
                .status(po.getStatus())
                .orderedAt(po.getOrderedAt())
                .expectedAt(po.getExpectedAt())
                .receivedAt(po.getReceivedAt())
                .notes(po.getNotes())
                .subtotal(po.getSubtotal())
                .shippingCost(po.getShippingCost())
                .tax(po.getTax())
                .total(po.getTotal())
                .items(itemResponses)
                .build();
    }
}
