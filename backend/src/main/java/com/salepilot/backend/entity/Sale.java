package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Sale entity representing a transaction in the POS system.
 * Can be for POS or online sales, with payment and fulfillment tracking.
 */
@Entity
@Table(name = "sales", indexes = {
        @Index(name = "idx_sales_store_id", columnList = "store_id"),
        @Index(name = "idx_sales_store_id_timestamp", columnList = "store_id, timestamp"),
        @Index(name = "idx_sales_fulfillment_status", columnList = "fulfillment_status"),
        @Index(name = "idx_sales_customer_id", columnList = "customer_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale extends TenantAware {

    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId; // Unique transaction identifier

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "tax", nullable = false, precision = 10, scale = 2)
    private BigDecimal tax;

    @Column(name = "discount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "store_credit_used", precision = 10, scale = 2)
    private BigDecimal storeCreditUsed;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PAID;

    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_status", nullable = false)
    @Builder.Default
    private FulfillmentStatus fulfillmentStatus = FulfillmentStatus.FULFILLED;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    @Builder.Default
    private SalesChannel channel = SalesChannel.POS;

    @Column(name = "customer_details", columnDefinition = "jsonb")
    private String customerDetails; // JSON object with name, email, phone, address

    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "due_date")
    private LocalDate dueDate; // For invoicing

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status", nullable = false)
    @Builder.Default
    private RefundStatus refundStatus = RefundStatus.NONE;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }

    /**
     * Payment status enumeration
     */
    public enum PaymentStatus {
        PAID,
        UNPAID,
        PARTIALLY_PAID
    }

    /**
     * Fulfillment status enumeration
     */
    public enum FulfillmentStatus {
        PENDING,
        FULFILLED,
        SHIPPED,
        CANCELLED
    }

    /**
     * Sales channel enumeration
     */
    public enum SalesChannel {
        POS, // Point of Sale
        ONLINE // Online store
    }

    /**
     * Refund status enumeration
     */
    public enum RefundStatus {
        NONE,
        PARTIALLY_REFUNDED,
        FULLY_REFUNDED
    }
}
