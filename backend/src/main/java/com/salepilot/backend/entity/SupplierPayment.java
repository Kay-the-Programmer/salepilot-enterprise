package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Supplier Payment entity for tracking payments made to suppliers.
 */
@Entity
@Table(name = "supplier_payments", indexes = {
        @Index(name = "idx_supplier_payments_store_id", columnList = "store_id"),
        @Index(name = "idx_supplier_payments_invoice_id", columnList = "supplier_invoice_id"),
        @Index(name = "idx_supplier_payments_date", columnList = "date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierPayment extends TenantAware {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_invoice_id", nullable = false)
    private SupplierInvoice supplierInvoice;

    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "method", nullable = false)
    private String method; // e.g., "Check", "Bank Transfer", "Cash"

    @Column(name = "reference")
    private String reference; // Check number, transaction reference

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = Instant.now();
        }
    }
}
