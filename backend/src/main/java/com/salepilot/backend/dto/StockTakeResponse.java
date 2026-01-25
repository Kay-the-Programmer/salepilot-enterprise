package com.salepilot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Response DTO for Stock Take details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTakeResponse {

    private Long id;
    private Instant startTime;
    private Instant endTime;
    private String status;
    private List<StockTakeItemResponse> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockTakeItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String sku;
        private BigDecimal expected;
        private BigDecimal counted;
        private BigDecimal discrepancy;
        private boolean hasDiscrepancy;
    }
}
