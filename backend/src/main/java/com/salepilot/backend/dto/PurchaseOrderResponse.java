package com.salepilot.backend.dto;

import com.salepilot.backend.entity.PurchaseOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Response DTO for Purchase Order details is
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderResponse {

    private Long id;
    private String poNumber;
    private Long supplierId;
    private String supplierName;
    private PurchaseOrder.POStatus status;
    private Instant orderedAt;
    private Instant expectedAt;
    private Instant receivedAt;
    private String notes;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal tax;
    private BigDecimal total;
    private List<PurchaseOrderItemResponse> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseOrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String sku;
        private BigDecimal quantity;
        private BigDecimal costPrice;
        private BigDecimal receivedQuantity;
        private BigDecimal total;
        private boolean fullyReceived;
    }
}
