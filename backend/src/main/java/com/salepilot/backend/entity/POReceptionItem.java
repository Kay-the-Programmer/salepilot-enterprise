package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * PO Reception Item entity representing items received in a specific reception
 * event.
 */
@Entity
@Table(name = "po_reception_items", indexes = {
        @Index(name = "idx_po_reception_items_store_id", columnList = "store_id"),
        @Index(name = "idx_po_reception_items_reception_id", columnList = "reception_id"),
        @Index(name = "idx_po_reception_items_product_id", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POReceptionItem extends TenantAware {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reception_id", nullable = false)
    private POReception reception;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name", nullable = false)
    private String productName; // Snapshot

    @Column(name = "quantity_received", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantityReceived;
}
