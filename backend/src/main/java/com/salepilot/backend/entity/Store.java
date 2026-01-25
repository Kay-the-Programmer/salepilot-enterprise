package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Store entity representing individual tenants in the multi-tenant system.
 * Each store represents a separate business using the SalePilot platform.
 */
@Entity
@Table(name = "stores", indexes = {
        @Index(name = "idx_stores_status", columnList = "status"),
        @Index(name = "idx_stores_subscription_status", columnList = "subscription_status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private StoreStatus status = StoreStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status", nullable = false)
    @Builder.Default
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.TRIAL;

    @Column(name = "subscription_ends_at")
    private Instant subscriptionEndsAt;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "verification_documents", columnDefinition = "jsonb")
    private String verificationDocuments; // JSON array of document objects

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    /**
     * Store status enumeration
     */
    public enum StoreStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED
    }

    /**
     * Subscription status enumeration
     */
    public enum SubscriptionStatus {
        TRIAL,
        ACTIVE,
        PAST_DUE,
        CANCELED
    }
}
