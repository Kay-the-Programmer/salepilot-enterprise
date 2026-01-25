package com.salepilot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Response DTO for Return details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnResponse {

    private Long id;
    private String returnId;
    private Long originalSaleId;
    private String originalTransactionId;
    private Instant timestamp;
    private BigDecimal refundAmount;
    private String refundMethod;
    private List<ReturnItemResponse> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private BigDecimal quantity;
        private String reason;
        private boolean addToStock;
    }
}
