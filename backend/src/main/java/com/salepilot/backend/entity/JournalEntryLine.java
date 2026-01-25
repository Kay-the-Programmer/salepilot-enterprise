package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Journal Entry Line entity representing individual debit/credit lines.
 * Part of double-entry bookkeeping where debits must equal credits.
 */
@Entity
@Table(name = "journal_entry_lines", indexes = {
        @Index(name = "idx_journal_entry_lines_store_id_jeid", columnList = "store_id, journal_entry_id"),
        @Index(name = "idx_journal_entry_lines_account_id", columnList = "account_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntryLine extends TenantAware {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_entry_id", nullable = false)
    private JournalEntry journalEntry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private LineType type; // DEBIT or CREDIT

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "account_name", nullable = false)
    private String accountName; // Denormalized for display

    /**
     * Line type enumeration
     */
    public enum LineType {
        DEBIT,
        CREDIT
    }
}
