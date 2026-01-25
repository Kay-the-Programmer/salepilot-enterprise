package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Stock Take entity representing a physical inventory count session.
 */
@Entity
@Table(name = "stock_takes", indexes = {
        @Index(name = "idx_stock_takes_store_id", columnList = "store_id"),
        @Index(name = "idx_stock_takes_start_time", columnList = "start_time")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTake extends TenantAware {

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private StockTakeStatus status = StockTakeStatus.ACTIVE;

    @PrePersist
    protected void onCreate() {
        if (startTime == null) {
            startTime = Instant.now();
        }
    }

    /**
     * Complete the stock take
     */
    public void complete() {
        this.status = StockTakeStatus.COMPLETED;
        this.endTime = Instant.now();
    }

    /**
     * Stock take status enumeration
     */
    public enum StockTakeStatus {
        ACTIVE,
        COMPLETED
    }
}
