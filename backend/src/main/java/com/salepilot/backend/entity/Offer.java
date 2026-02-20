package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Offer entity for location-based marketplace features.
 * Allows users to create and accept offers for products/services.
 */
@Entity
@Table(name = "offers", indexes = {
        @Index(name = "idx_offers_user_id", columnList = "user_id"),
        @Index(name = "idx_offers_status", columnList = "status"),
        @Index(name = "idx_offers_accepted_by", columnList = "accepted_by"),
        @Index(name = "idx_offers_store_id", columnList = "store_id"),
        @Index(name = "idx_offers_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Offer extends TenantAware {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User who created the offer

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude; // Location-based

    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private OfferStatus status = OfferStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accepted_by")
    private User acceptedBy; // User who accepted the offer

    /**
     * Offer status enumeration
     */
    public enum OfferStatus {
        OPEN,
        ACCEPTED,
        COMPLETED,
        CANCELLED
    }
}
