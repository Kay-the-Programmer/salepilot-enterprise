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
import java.util.List;

/**
 * Request DTO for processing a return/refund
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequest {

    @NotNull(message = "Original Sale ID is required")
    private Long saleId;

    @NotEmpty(message = "Return must have at least one item")
    @Valid
    private List<ReturnItemRequest> items;

    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.0", message = "Refund amount cannot be negative")
    private BigDecimal refundAmount;

    @NotEmpty(message = "Refund method is required")
    private String refundMethod; // "Cash", "Store Credit", etc.

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @DecimalMin(value = "0.001", message = "Quantity must be greater than zero")
        private BigDecimal quantity;

        private String reason;

        private boolean addToStock;
    }
}
