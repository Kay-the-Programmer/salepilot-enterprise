package com.salepilot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Dashboard Metrics (Home Screen)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricsDTO {

    private BigDecimal dailySales;
    private Long dailyTransactionCount;
    private BigDecimal monthlySales;

    private Long lowStockCount;
    private Long outOfStockCount;

    // Recent activity could be added here
    private List<TopProductDTO> topProducts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProductDTO {
        private String name;
        private BigDecimal quantitySold;
        private BigDecimal revenue;
    }
}
