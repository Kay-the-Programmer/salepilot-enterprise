package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * System Notification entity for system-wide announcements (not tenant-scoped).
 */
@Entity
@Table(name = "system_notifications", indexes = {
        @Index(name = "idx_system_notifications_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemNotification extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "text")
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy; // Admin/superadmin who created it

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
