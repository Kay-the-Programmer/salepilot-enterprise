package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.dto.NotificationDTO;
import com.salepilot.backend.entity.Notification;
import com.salepilot.backend.entity.User;
import com.salepilot.backend.exception.NotFoundException;
import com.salepilot.backend.repository.NotificationRepository;
import com.salepilot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing user notifications.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * Send notification to a user
     */
    public Notification sendNotification(Long recipientId, String title, String message,
            Notification.NotificationType type, String link) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new NotFoundException("Recipient not found"));

        Notification notification = Notification.builder()
                .recipient(recipient)
                .title(title)
                .message(message)
                .type(type)
                .link(link)
                .build();

        // TenantContext is assumed to be active store.
        // If system background job (e.g. low stock scanner), ensure context is set.

        return notificationRepository.save(notification);
    }

    /**
     * Get user notifications
     */
    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        String storeId = TenantContext.getCurrentTenant();
        return notificationRepository.findByStoreIdAndRecipient_IdOrderByCreatedAtDesc(storeId, userId, pageable);
    }

    /**
     * Mark as read
     */
    public void markAsRead(Long notificationId) {
        String storeId = TenantContext.getCurrentTenant();
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));

        if (!n.getStoreId().equals(storeId))
            throw new SecurityException("Unauthorized");

        n.setIsRead(true);
        notificationRepository.save(n);
    }

    public void markAllAsRead(Long userId) {
        // Implementation left as exercise (Query needed)
    }
}
