package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Supplier Invoice entity for tracking invoices from suppliers (Accounts
 * Payable).
 */
@Entity
@Table(name = "supplier_invoices", indexes = {
        @Index(name = "idx_supplier_invoices_store_id", columnList = "store_id"),
        @Index(name = "idx_supplier_invoices_supplier_id", columnList = "supplier_id"),
        @Index(name = "idx_supplier_invoices_po_id", columnList = "purchase_order_id"),
        @Index(name = "idx_supplier_invoices_invoice_number", columnList = "invoice_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierInvoice extends TenantAware {

    @Column(name = "invoice_number", nullable = false)
    private String invoiceNumber; // From the supplier

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "supplier_name", nullable = false)
    private String supplierName; // Denormalized

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @Column(name = "po_number", nullable = false)
    private String poNumber; // Denormalized

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.UNPAID;

    /**
     * Calculate outstanding amount
     */
    public BigDecimal getOutstandingAmount() {
        return amount.subtract(amountPaid);
    }

    /**
     * Check if invoice is overdue
     */
    public boolean isOverdue() {
        return status != InvoiceStatus.PAID && dueDate.isBefore(LocalDate.now());
    }

    /**
     * Invoice status enumeration
     */
    public enum InvoiceStatus {
        UNPAID,
        PARTIALLY_PAID,
        PAID,
        OVERDUE
    }
}
