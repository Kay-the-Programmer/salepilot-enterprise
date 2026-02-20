package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Customer entity for managing customer information, store credit, and A/R
 * balance.
 */
@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customers_store_id", columnList = "store_id"),
        @Index(name = "idx_customers_store_id_created_at", columnList = "store_id, created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends TenantAware {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address", columnDefinition = "jsonb")
    private String address; // JSON object with street, city, state, zip

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Column(name = "store_credit", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal storeCredit = BigDecimal.ZERO;

    @Column(name = "account_balance", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal accountBalance = BigDecimal.ZERO; // A/R balance (negative = customer owes)

    /**
     * Check if customer has outstanding balance
     */
    public boolean hasOutstandingBalance() {
        return accountBalance.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Get absolute value of outstanding balance
     */
    public BigDecimal getOutstandingAmount() {
        return accountBalance.abs();
    }
}
