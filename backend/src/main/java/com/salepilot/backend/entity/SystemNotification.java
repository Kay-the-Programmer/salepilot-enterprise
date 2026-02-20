package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    private User creator; // Admin/superadmin who created it (Read-only view)

}
