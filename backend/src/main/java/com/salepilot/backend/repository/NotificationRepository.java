package com.salepilot.backend.repository;

import com.salepilot.backend.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Notification entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find notifications for a user in a store
     */
    Page<Notification> findByStoreIdAndRecipient_IdOrderByCreatedAtDesc(String storeId, Long recipientId,
            Pageable pageable);

    /**
     * Count unread notifications
     */
    Long countByStoreIdAndRecipient_IdAndIsReadFalse(String storeId, Long recipientId);
}
