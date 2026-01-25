package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Journal Entry entity for double-entry bookkeeping transactions.
 * Each transaction must have balanced debits and credits.
 */
@Entity
@Table(name = "journal_entries", indexes = {
        @Index(name = "idx_journal_entries_store_id_date", columnList = "store_id, date"),
        @Index(name = "idx_journal_entries_source_type", columnList = "source_type"),
        @Index(name = "idx_journal_entries_source_id", columnList = "source_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntry extends TenantAware {

    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private SourceType sourceType;

    @Column(name = "source_id")
    private String sourceId; // Reference to sale.transactionId, po.id, etc.

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = Instant.now();
        }
    }

    /**
     * Source type for journal entries
     */
    public enum SourceType {
        SALE,
        PURCHASE,
        MANUAL,
        PAYMENT
    }
}
