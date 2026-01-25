package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Product entity representing items for sale in the POS system.
 * Includes SKU, barcode, pricing, stock, and image management.
 */
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_products_store_id", columnList = "store_id"),
        @Index(name = "idx_products_store_id_status", columnList = "store_id, status"),
        @Index(name = "idx_products_sku", columnList = "sku"),
        @Index(name = "idx_products_barcode", columnList = "barcode")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends TenantAware {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "sku", nullable = false)
    private String sku; // Stock Keeping Unit

    @Column(name = "barcode")
    private String barcode; // Barcode/UPC

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Retail/selling price

    @Column(name = "cost_price", precision = 10, scale = 2)
    private BigDecimal costPrice; // Cost from supplier

    @Column(name = "stock", nullable = false, precision = 10, scale = 3)
    @Builder.Default
    private BigDecimal stock = BigDecimal.ZERO; // Current stock level

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measure", nullable = false)
    @Builder.Default
    private UnitOfMeasure unitOfMeasure = UnitOfMeasure.UNIT;

    @Column(name = "image_urls", columnDefinition = "text[]")
    private String[] imageUrls; // Array of image URLs

    @Column(name = "brand")
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(name = "reorder_point")
    private Integer reorderPoint; // Alert when stock falls below this

    @Column(name = "weight", precision = 10, scale = 3)
    private BigDecimal weight; // in kg

    @Column(name = "dimensions")
    private String dimensions; // e.g., "W x H x D cm"

    @Column(name = "safety_stock")
    private Integer safetyStock; // Minimum stock to maintain

    @Column(name = "variants", columnDefinition = "jsonb")
    private String variants; // JSON array of product variants

    @Column(name = "custom_attributes", columnDefinition = "jsonb")
    private String customAttributes; // JSON object with custom attributes

    /**
     * Check if product is low on stock
     */
    public boolean isLowStock() {
        if (reorderPoint == null) {
            return false;
        }
        return stock.compareTo(BigDecimal.valueOf(reorderPoint)) <= 0;
    }

    /**
     * Calculate profit margin
     */
    public BigDecimal getProfitMargin() {
        if (costPrice == null || costPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return price.subtract(costPrice)
                .divide(price, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Unit of measure enumeration
     */
    public enum UnitOfMeasure {
        UNIT, // Individual units
        KG // Kilograms (for weight-based products)
    }

    /**
     * Product status enumeration
     */
    public enum ProductStatus {
        ACTIVE,
        ARCHIVED
    }
}
