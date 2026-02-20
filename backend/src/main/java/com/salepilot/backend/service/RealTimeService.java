package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service to dispatch Real-Time messages via WebSocket.
 */
@Service
@RequiredArgsConstructor
public class RealTimeService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Notify store about new sale
     */
    public void notifyNewSale(String storeId, Long saleId, java.math.BigDecimal amount) {
        // Topic: /topic/sales/{storeId}
        String destination = "/topic/sales/" + storeId;
        String message = String.format("{\"type\":\"NEW_SALE\",\"id\":%d,\"amount\":%.2f}", saleId, amount);
        messagingTemplate.convertAndSend(destination, message);
    }

    /**
     * Notify store about low stock (could be used for Kitchen Display)
     */
    public void notifyLowStock(String storeId, String productName) {
        String destination = "/topic/inventory/" + storeId;
        String message = String.format("{\"type\":\"LOW_STOCK\",\"product\":\"%s\"}", productName);
        messagingTemplate.convertAndSend(destination, message);
    }

    /**
     * General notification
     */
    public void sendToUser(String username, String message) {
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", message);
    }
}
