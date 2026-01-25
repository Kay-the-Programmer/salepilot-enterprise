package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Purchase Order entity for managing orders to suppliers.
 * Supports draft, ordered, partial receipt, and completion workflows.
 */
@Entity
@Table(name = "purchase_orders", indexes = {
        @Index(name = "idx_purchase_orders_store_id", columnList = "store_id"),
        @Index(name = "idx_purchase_orders_store_id_created_at", columnList = "store_id, created_at"),
        @Index(name = "idx_purchase_orders_po_number", columnList = "po_number", unique = true),
        @Index(name = "idx_purchase_orders_supplier_id", columnList = "supplier_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder extends TenantAware {

    @Column(name = "po_number", nullable = false, unique = true)
    private String poNumber; // e.g., "PO-2024-001"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "supplier_name", nullable = false)
    private String supplierName; // Denormalized for easy display

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private POStatus status = POStatus.DRAFT;

    @Column(name = "ordered_at")
    private Instant orderedAt;

    @Column(name = "expected_at")
    private Instant expectedAt;

    @Column(name = "received_at")
    private Instant receivedAt;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "shipping_cost", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Column(name = "tax", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    /**
     * Purchase order status enumeration
     */
    public enum POStatus {
        DRAFT,
        ORDERED,
        PARTIALLY_RECEIVED,
        RECEIVED,
        CANCELED
    }
}
