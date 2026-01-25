package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Payment entity for tracking payments made against sales.
 * Supports partial payments and multiple payment methods.
 */
@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payments_store_id", columnList = "store_id"),
        @Index(name = "idx_payments_store_id_date", columnList = "store_id, date"),
        @Index(name = "idx_payments_sale_id", columnList = "sale_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends TenantAware {

    @Column(name = "payment_id", nullable = false, unique = true)
    private String paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "method", nullable = false)
    private String method; // e.g., "Cash", "Credit Card", "Mobile Payment"

    @Column(name = "reference")
    private String reference; // Transaction reference number

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = Instant.now();
        }
    }
}
