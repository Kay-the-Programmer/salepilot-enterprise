package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Return item entity representing individual line items in a product return.
 */
@Entity
@Table(name = "return_items", indexes = {
        @Index(name = "idx_return_items_store_id", columnList = "store_id"),
        @Index(name = "idx_return_items_return_id", columnList = "return_id"),
        @Index(name = "idx_return_items_product_id", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnItem extends TenantAware {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_id", nullable = false)
    private Return returnRecord; // Renamed to avoid conflict with Java keyword

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name", nullable = false)
    private String productName; // Snapshot at time of return

    @Column(name = "quantity", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;

    @Column(name = "reason")
    private String reason; // Reason for return

    @Column(name = "add_to_stock", nullable = false)
    @Builder.Default
    private Boolean addToStock = false; // Whether to add back to inventory
}
