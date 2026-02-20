package com.salepilot.backend.dto;

import com.salepilot.backend.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Superadmin View DTO for Stores
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStoreDTO {
    private Long id;
    private String name;
    private Store.StoreStatus status;
    private Store.SubscriptionStatus subscriptionStatus;
    private java.time.LocalDateTime subscriptionEndsAt;
    private Boolean isVerified;
    private java.time.LocalDateTime createdAt;

    // Aggregated stats (optional)
    private Long userCount;
    private Long productCount;
}
