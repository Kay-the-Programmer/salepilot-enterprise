package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Sale item entity representing individual line items in a sale.
 */
@Entity
@Table(name = "sale_items", indexes = {
        @Index(name = "idx_sale_items_store_id", columnList = "store_id"),
        @Index(name = "idx_sale_items_store_id_sale_id", columnList = "store_id, sale_id"),
        @Index(name = "idx_sale_items_product_id", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleItem extends TenantAware {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;

    @Column(name = "price_at_sale", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtSale; // Price at time of sale (snapshot)

    @Column(name = "cost_at_sale", precision = 10, scale = 2)
    private BigDecimal costAtSale; // Cost at time of sale (for profit calc)

    /**
     * Calculate line total
     */
    public BigDecimal getLineTotal() {
        return priceAtSale.multiply(quantity);
    }

    /**
     * Calculate line profit
     */
    public BigDecimal getLineProfit() {
        if (costAtSale == null) {
            return BigDecimal.ZERO;
        }
        return priceAtSale.subtract(costAtSale).multiply(quantity);
    }
}
