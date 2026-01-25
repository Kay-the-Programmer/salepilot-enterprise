package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Return entity for processing product returns and refunds.
 */
@Entity
@Table(name = "returns", indexes = {
        @Index(name = "idx_returns_store_id", columnList = "store_id"),
        @Index(name = "idx_returns_original_sale_id", columnList = "original_sale_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Return extends TenantAware {

    @Column(name = "return_id", nullable = false, unique = true)
    private String returnId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_sale_id", nullable = false)
    private Sale originalSale;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "refund_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_method", nullable = false)
    private String refundMethod; // e.g., "Cash", "Store Credit", "Original Payment Method"

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}
