package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Stock Take Item entity representing products counted during a stock take.
 */
@Entity
@Table(name = "stock_take_items", indexes = {
        @Index(name = "idx_stock_take_items_store_id", columnList = "store_id"),
        @Index(name = "idx_stock_take_items_stock_take_id", columnList = "stock_take_id"),
        @Index(name = "idx_stock_take_items_product_id", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTakeItem extends TenantAware {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_take_id", nullable = false)
    private StockTake stockTake;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "name", nullable = false)
    private String name; // Snapshot

    @Column(name = "sku", nullable = false)
    private String sku; // Snapshot

    @Column(name = "expected", nullable = false, precision = 10, scale = 3)
    private BigDecimal expected; // Expected stock from system

    @Column(name = "counted", precision = 10, scale = 3)
    private BigDecimal counted; // Actual counted stock (null = not yet counted)

    /**
     * Calculate discrepancy (counted - expected)
     */
    public BigDecimal getDiscrepancy() {
        if (counted == null) {
            return BigDecimal.ZERO;
        }
        return counted.subtract(expected);
    }

    /**
     * Check if item has discrepancy
     */
    public boolean hasDiscrepancy() {
        if (counted == null) {
            return false;
        }
        return counted.compareTo(expected) != 0;
    }
}
