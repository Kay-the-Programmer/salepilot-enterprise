package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Subscription Plan entity (SAAS Plans).
 * This is a Global entity (not TenantAware).
 */
@Entity
@Table(name = "subscription_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name; // e.g., "Free", "Pro", "Enterprise"

    @Column(name = "code", nullable = false, unique = true)
    private String code; // e.g., "FREE_TIER"

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "interval", nullable = false)
    private String interval; // "MONTHLY", "YEARLY"

    @Column(name = "description")
    private String description;

    @Column(name = "features", columnDefinition = "text")
    private String features; // JSON list of features

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
