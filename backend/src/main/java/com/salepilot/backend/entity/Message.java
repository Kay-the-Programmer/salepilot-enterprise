package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Message entity for internal user-to-user communication.
 */
@Entity
@Table(name = "messages", indexes = {
        @Index(name = "idx_messages_store_id", columnList = "store_id"),
        @Index(name = "idx_messages_sender_id", columnList = "sender_id"),
        @Index(name = "idx_messages_recipient_id", columnList = "recipient_id"),
        @Index(name = "idx_messages_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message extends TenantAware {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    // Optional: Link to an entity (polymorphic or specific)
    @Column(name = "related_entity_type")
    private String relatedEntityType; // e.g., "OFFER"

    @Column(name = "related_entity_id")
    private String relatedEntityId;

}
