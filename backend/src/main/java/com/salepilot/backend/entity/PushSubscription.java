package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity to store Web Push Subscriptions for users.
 * Corresponds to the browser's PushSubscription object.
 */
@Entity
@Table(name = "push_subscriptions", indexes = {
        @Index(name = "idx_push_subs_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushSubscription extends TenantAware {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "endpoint", nullable = false, columnDefinition = "text")
    private String endpoint;

    @Column(name = "p256dh", nullable = false)
    private String p256dh; // Public key

    @Column(name = "auth", nullable = false)
    private String auth; // Auth secret

    @Column(name = "user_agent")
    private String userAgent;

}
