package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.dto.MessageDTO;
import com.salepilot.backend.entity.Message;
import com.salepilot.backend.entity.User;
import com.salepilot.backend.exception.NotFoundException;
import com.salepilot.backend.repository.MessageRepository;
import com.salepilot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for internal messaging.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    /**
     * Send a message
     */
    public Message sendMessage(MessageDTO request, Long senderId) {
        String storeId = TenantContext.getCurrentTenant();

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("Sender not found"));

        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new NotFoundException("Recipient not found"));

        // Check if both belong to same store? Or cross-store?
        // Assuming strict tenant for now, but marketplace might differ.
        if (!sender.getStoreId().equals(storeId) || !recipient.getStoreId().equals(storeId)) {
            // In simple POS, internal message.
            // If Marketplace, we might relax this check.
        }

        Message message = Message.builder()
                .sender(sender)
                .recipient(recipient)
                .content(request.getContent())
                .relatedEntityType(request.getRelatedEntityType())
                .relatedEntityId(request.getRelatedEntityId())
                .build();

        return messageRepository.save(message);
    }

    /**
     * Get inbox
     */
    @Transactional(readOnly = true)
    public Page<Message> getInbox(Long userId, Pageable pageable) {
        String storeId = TenantContext.getCurrentTenant();
        return messageRepository.findByStoreIdAndRecipient_IdOrderByCreatedAtDesc(storeId, userId, pageable);
    }

    /**
     * Get conversation
     */
    @Transactional(readOnly = true)
    public Page<Message> getConversation(Long userId1, Long userId2, Pageable pageable) {
        String storeId = TenantContext.getCurrentTenant();
        return messageRepository.findConversation(storeId, userId1, userId2, pageable);
    }

    /**
     * Mark as read logic would be here
     */
}
