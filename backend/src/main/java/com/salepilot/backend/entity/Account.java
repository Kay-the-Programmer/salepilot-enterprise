package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Account entity for the Chart of Accounts in double-entry bookkeeping.
 * Tracks account balances and supports various account types.
 */
@Entity
@Table(name = "accounts", indexes = {
        @Index(name = "idx_accounts_store_id", columnList = "store_id"),
        @Index(name = "uidx_accounts_store_number", columnList = "store_id, number", unique = true),
        @Index(name = "uidx_accounts_store_sub_type", columnList = "store_id, sub_type", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends TenantAware {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "number", nullable = false)
    private String number; // e.g., "1010", "4010"

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AccountType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "sub_type")
    private AccountSubType subType; // For automatic transaction mapping

    @Column(name = "balance", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "is_debit_normal", nullable = false)
    private Boolean isDebitNormal; // true for assets/expenses, false for liability/equity/revenue

    @Column(name = "description")
    private String description;

    /**
     * Update balance based on transaction type
     */
    public void updateBalance(BigDecimal amount, boolean isDebit) {
        if (isDebitNormal) {
            balance = isDebit ? balance.add(amount) : balance.subtract(amount);
        } else {
            balance = isDebit ? balance.subtract(amount) : balance.add(amount);
        }
    }

    /**
     * Account type enumeration
     */
    public enum AccountType {
        ASSET,
        LIABILITY,
        EQUITY,
        REVENUE,
        EXPENSE
    }

    /**
     * Account sub-type for automatic mapping
     */
    public enum AccountSubType {
        CASH,
        ACCOUNTS_RECEIVABLE,
        INVENTORY,
        ACCOUNTS_PAYABLE,
        SALES_TAX_PAYABLE,
        SALES_REVENUE,
        COGS,
        STORE_CREDIT_PAYABLE,
        INVENTORY_ADJUSTMENT
    }
}
