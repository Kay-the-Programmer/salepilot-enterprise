package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * PO Reception entity representing a goods receipt event for a purchase order.
 * Multiple receptions can occur for partial deliveries.
 */
@Entity
@Table(name = "po_receptions", indexes = {
        @Index(name = "idx_po_receptions_store_id", columnList = "store_id"),
        @Index(name = "idx_po_receptions_store_id_po_id", columnList = "store_id, po_id"),
        @Index(name = "idx_po_receptions_reception_date", columnList = "reception_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POReception extends TenantAware {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @Column(name = "reception_date", nullable = false)
    private Instant receptionDate;

    @PrePersist
    protected void onCreate() {
        if (receptionDate == null) {
            receptionDate = Instant.now();
        }
    }
}
