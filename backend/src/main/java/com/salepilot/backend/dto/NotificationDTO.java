package com.salepilot.backend.dto;

import com.salepilot.backend.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Notification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private Notification.NotificationType type;
    private Boolean isRead;
    private String link;
    private LocalDateTime createdAt;
}
