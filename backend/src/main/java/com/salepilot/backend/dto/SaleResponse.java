package com.salepilot.backend.dto;

import com.salepilot.backend.entity.Sale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for sale transaction details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponse {

    private Long id;
    private String transactionId;
    private Instant timestamp;
    private Long customerId;
    private String customerName;
    private BigDecimal total;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal storeCreditUsed;
    private Sale.PaymentStatus paymentStatus;
    private Sale.FulfillmentStatus fulfillmentStatus;
    private Sale.SalesChannel channel;
    private BigDecimal amountPaid;
    private BigDecimal balanceDue;
    private LocalDate dueDate;
    private Sale.RefundStatus refundStatus;
    private List<SaleItemResponse> items;
    private List<PaymentResponse> payments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String sku;
        private BigDecimal quantity;
        private BigDecimal price;
        private BigDecimal total;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentResponse {
        private String id;
        private Instant date;
        private BigDecimal amount;
        private String method;
        private String reference;
    }
}
