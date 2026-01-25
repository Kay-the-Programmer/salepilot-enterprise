package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Expense entity for tracking business expenses.
 */
@Entity
@Table(name = "expenses", indexes = {
        @Index(name = "idx_expenses_store_id", columnList = "store_id"),
        @Index(name = "idx_expenses_date", columnList = "date"),
        @Index(name = "idx_expenses_expense_account_id", columnList = "expense_account_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense extends TenantAware {

    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_account_id", nullable = false)
    private Account expenseAccount;

    @Column(name = "expense_account_name", nullable = false)
    private String expenseAccountName; // Denormalized

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_account_id", nullable = false)
    private Account paymentAccount; // Cash or Accounts Payable

    @Column(name = "payment_account_name", nullable = false)
    private String paymentAccountName; // Denormalized

    @Column(name = "category")
    private String category; // e.g., "Rent", "Utilities", "Supplies"

    @Column(name = "reference")
    private String reference; // Invoice number, receipt number

    @Column(name = "created_by", nullable = false)
    private String createdBy; // User ID who created the expense

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = Instant.now();
        }
    }
}
