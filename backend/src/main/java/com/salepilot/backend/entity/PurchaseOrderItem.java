package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Purchase Order Item entity representing line items in a purchase order.
 */
@Entity
@Table(name = "purchase_order_items", indexes = {
        @Index(name = "idx_po_items_store_id", columnList = "store_id"),
        @Index(name = "idx_po_items_store_id_po_id", columnList = "store_id, po_id"),
        @Index(name = "idx_po_items_product_id", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderItem extends TenantAware {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name", nullable = false)
    private String productName; // Snapshot

    @Column(name = "sku", nullable = false)
    private String sku; // Snapshot

    @Column(name = "quantity", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;

    @Column(name = "cost_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPrice; // Cost at time of order

    @Column(name = "received_quantity", nullable = false, precision = 10, scale = 3)
    @Builder.Default
    private BigDecimal receivedQuantity = BigDecimal.ZERO;

    /**
     * Calculate line total
     */
    public BigDecimal getLineTotal() {
        return costPrice.multiply(quantity);
    }

    /**
     * Check if item fully received
     */
    public boolean isFullyReceived() {
        return receivedQuantity.compareTo(quantity) >= 0;
    }

    /**
     * Calculate remaining to receive
     */
    public BigDecimal getRemainingQuantity() {
        return quantity.subtract(receivedQuantity);
    }
}
