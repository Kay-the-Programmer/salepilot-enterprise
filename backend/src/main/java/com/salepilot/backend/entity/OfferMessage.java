package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Offer Message entity for chat/messaging within offers.
 */
@Entity
@Table(name = "offer_messages", indexes = {
        @Index(name = "idx_offer_messages_offer_id", columnList = "offer_id"),
        @Index(name = "idx_offer_messages_sender_id", columnList = "sender_id"),
        @Index(name = "idx_offer_messages_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "image_url")
    private String imageUrl; // Optional image attachment

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
