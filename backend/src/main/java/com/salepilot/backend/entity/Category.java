package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Product category entity with hierarchical parent-child structure.
 * Categories can have custom attributes for products.
 */
@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_categories_store_id", columnList = "store_id"),
        @Index(name = "idx_categories_parent_id", columnList = "parent_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends TenantAware {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent; // Parent category for hierarchical structure

    @Column(name = "attributes", columnDefinition = "jsonb")
    private String attributes; // JSON array of custom attribute definitions

    @Column(name = "revenue_account_id")
    private String revenueAccountId; // Accounting integration

    @Column(name = "cogs_account_id")
    private String cogsAccountId; // Cost of Goods Sold account

    /**
     * Get full category path (e.g., "Electronics > Phones > Smartphones")
     */
    public String getFullPath() {
        if (parent == null) {
            return name;
        }
        return parent.getFullPath() + " > " + name;
    }
}
