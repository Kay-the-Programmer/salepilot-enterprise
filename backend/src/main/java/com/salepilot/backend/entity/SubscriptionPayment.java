package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Subscription Payment entity for tracking subscription revenue from stores.
 * System-level entity (not tenant-scoped).
 */
@Entity
@Table(name = "subscription_payments", indexes = {
        @Index(name = "idx_subscription_payments_store_id", columnList = "store_id"),
        @Index(name = "idx_subscription_payments_paid_at", columnList = "paid_at"),
        @Index(name = "idx_subscription_payments_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPayment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency; // e.g., "USD", "EUR"

    @Column(name = "period_start")
    private Instant periodStart;

    @Column(name = "period_end")
    private Instant periodEnd;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "method")
    private String method; // Payment method used

    @Column(name = "reference")
    private String reference; // Payment reference/transaction ID

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

}
