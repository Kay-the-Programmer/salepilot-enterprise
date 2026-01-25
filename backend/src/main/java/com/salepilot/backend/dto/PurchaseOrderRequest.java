package com.salepilot.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Request DTO for creating/updating Purchase Orders
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderRequest {

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    private Instant expectedAt;

    private String notes;

    @DecimalMin(value = "0.0", message = "Shipping cost cannot be negative")
    private BigDecimal shippingCost;

    @DecimalMin(value = "0.0", message = "Tax cannot be negative")
    private BigDecimal tax;

    @NotEmpty(message = "Purchase order must have at least one item")
    @Valid
    private List<PurchaseOrderItemRequest> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseOrderItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @DecimalMin(value = "0.001", message = "Quantity must be greater than zero")
        private BigDecimal quantity;

        @DecimalMin(value = "0.0", message = "Cost price cannot be negative")
        private BigDecimal costPrice;
    }
}
