package com.salepilot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Web Push Subscription from client
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushSubscriptionRequest {

    @NotBlank
    private String endpoint;

    private Keys keys;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Keys {
        private String p256dh;
        private String auth;
    }
}
