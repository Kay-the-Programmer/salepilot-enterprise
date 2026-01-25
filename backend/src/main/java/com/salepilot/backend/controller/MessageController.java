package com.salepilot.backend.controller;

import com.salepilot.backend.dto.MessageDTO;
import com.salepilot.backend.entity.Message;
import com.salepilot.backend.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Messaging.
 */
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "Internal messaging endpoints")
public class MessageController {

    private final MessageService messageService;

    private Long getCurrentUserId() {
        return 1L; // Placeholder
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Send a message")
    public ResponseEntity<MessageDTO> sendMessage(@Valid @RequestBody MessageDTO request) {
        Message message = messageService.sendMessage(request, getCurrentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(message));
    }

    @GetMapping("/inbox")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get my inbox")
    public ResponseEntity<Page<MessageDTO>> getInbox(Pageable pageable) {
        Page<Message> messages = messageService.getInbox(getCurrentUserId(), pageable);
        return ResponseEntity.ok(messages.map(this::mapToDTO));
    }

    private MessageDTO mapToDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFirstName())
                .recipientId(message.getRecipient().getId())
                .recipientName(message.getRecipient().getFirstName())
                .content(message.getContent())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .relatedEntityType(message.getRelatedEntityType())
                .relatedEntityId(message.getRelatedEntityId())
                .build();
    }
}
