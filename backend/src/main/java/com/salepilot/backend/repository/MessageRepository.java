package com.salepilot.backend.repository;

import com.salepilot.backend.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for Message entity.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Find conversation between two users
     */
    @Query("SELECT m FROM Message m WHERE m.storeId = :storeId AND " +
            "((m.sender.id = :user1Id AND m.recipient.id = :user2Id) OR " +
            "(m.sender.id = :user2Id AND m.recipient.id = :user1Id)) " +
            "ORDER BY m.createdAt DESC")
    Page<Message> findConversation(@Param("storeId") String storeId,
            @Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id,
            Pageable pageable);

    /**
     * Find inbox for a user
     */
    Page<Message> findByStoreIdAndRecipient_IdOrderByCreatedAtDesc(String storeId, Long recipientId, Pageable pageable);

    /**
     * Measure unread count
     */
    Long countByStoreIdAndRecipient_IdAndIsReadFalse(String storeId, Long recipientId);
}
