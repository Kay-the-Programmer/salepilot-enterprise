package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.dto.PushSubscriptionRequest;
import com.salepilot.backend.entity.PushSubscription;
import com.salepilot.backend.entity.User;
import com.salepilot.backend.exception.NotFoundException;
import com.salepilot.backend.repository.PushSubscriptionRepository;
import com.salepilot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing Web Push Subscriptions and sending messages.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PushNotificationService {

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final UserRepository userRepository;

    // In real app: Inject WebPush client (e.g. nl.martijndwars.webpush)

    public void subscribe(PushSubscriptionRequest request, Long userId) {
        String storeId = TenantContext.getCurrentTenant();

        // Check if exists
        if (pushSubscriptionRepository.findByEndpoint(request.getEndpoint()).isPresent()) {
            return; // Already subscribed
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        PushSubscription sub = PushSubscription.builder()
                .user(user)
                .endpoint(request.getEndpoint())
                .p256dh(request.getKeys().getP256dh())
                .auth(request.getKeys().getAuth())
                .build();

        // Ensure tenant context is set correctly?
        // PushSubscription is TenantAware, so it saves storeId.

        pushSubscriptionRepository.save(sub);
    }

    public void unsubscribe(String endpoint) {
        pushSubscriptionRepository.deleteByEndpoint(endpoint);
    }

    /**
     * Send push notification to user
     */
    public void sendPush(Long userId, String title, String message, String url) {
        List<PushSubscription> subs = pushSubscriptionRepository.findByUserId(userId);

        for (PushSubscription sub : subs) {
            try {
                // Mock sending
                // WebPushClient.send(sub.getEndpoint(), sub.getP256dh(), sub.getAuth(),
                // payload);
                System.out.println("Sending Push to " + userId + ": " + title);
            } catch (Exception e) {
                // If 410 Gone, remove subscription
                // pushSubscriptionRepository.delete(sub);
                System.err.println("Failed to push: " + e.getMessage());
            }
        }
    }
}
