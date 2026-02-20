package com.salepilot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for sending and viewing messages
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private Long id;
    private Long senderId;
    private String senderName;

    @NotNull(message = "Recipient ID is required")
    private Long recipientId;
    private String recipientName;

    @NotBlank(message = "Content is required")
    private String content;

    private Boolean isRead;
    private LocalDateTime createdAt;

    private String relatedEntityType;
    private String relatedEntityId;
}
