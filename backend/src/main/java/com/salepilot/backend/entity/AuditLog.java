package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Audit Log entity for tracking all user actions and system events.
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_logs_store_id_timestamp", columnList = "store_id, timestamp"),
        @Index(name = "idx_audit_logs_user_id", columnList = "user_id"),
        @Index(name = "idx_audit_logs_action", columnList = "action")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends TenantAware {

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_name", nullable = false)
    private String userName; // Denormalized for display

    @Column(name = "action", nullable = false)
    private String action; // e.g., "CREATE_PRODUCT", "UPDATE_SALE", "DELETE_CUSTOMER"

    @Column(name = "details", nullable = false, columnDefinition = "text")
    private String details; // JSON or text description

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}
