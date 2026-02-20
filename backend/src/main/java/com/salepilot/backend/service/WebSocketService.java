package com.salepilot.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for broadcasting real-time updates via WebSocket.
 * Matches Node.js socket.service.ts functionality.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Broadcast new sale event to store subscribers
     */
    public void broadcastNewSale(String storeId, Map<String, Object> saleData) {
        String destination = "/topic/store/" + storeId + "/sales";

        Map<String, Object> message = new HashMap<>();
        message.put("event", "sale:new");
        message.put("data", saleData);

        messagingTemplate.convertAndSend(destination, (Object) message);
        log.debug("Broadcasted new sale to store: {}", storeId);
    }

    /**
     * Broadcast inventory update to store subscribers
     */
    public void broadcastInventoryUpdate(String storeId, Map<String, Object> inventoryData) {
        String destination = "/topic/store/" + storeId + "/inventory";

        Map<String, Object> message = new HashMap<>();
        message.put("event", "inventory:updated");
        message.put("data", inventoryData);

        messagingTemplate.convertAndSend(destination, (Object) message);
        log.debug("Broadcasted inventory update to store: {}", storeId);
    }

    /**
     * Broadcast notification to store subscribers
     */
    public void broadcastNotification(String storeId, Map<String, Object> notificationData) {
        String destination = "/topic/store/" + storeId + "/notifications";

        Map<String, Object> message = new HashMap<>();
        message.put("event", "notification:new");
        message.put("data", notificationData);

        messagingTemplate.convertAndSend(destination, (Object) message);
        log.debug("Broadcasted notification to store: {}", storeId);
    }

    /**
     * Broadcast dashboard metrics update
     */
    public void broadcastMetricsUpdate(String storeId, Map<String, Object> metricsData) {
        String destination = "/topic/store/" + storeId + "/metrics";

        Map<String, Object> message = new HashMap<>();
        message.put("event", "metrics:updated");
        message.put("data", metricsData);

        messagingTemplate.convertAndSend(destination, (Object) message);
        log.debug("Broadcasted metrics update to store: {}", storeId);
    }

    /**
     * Broadcast product update
     */
    public void broadcastProductUpdate(String storeId, String productId, String action) {
        String destination = "/topic/store/" + storeId + "/products";

        Map<String, Object> message = new HashMap<>();
        message.put("event", "product:" + action);
        message.put("data", Map.of("productId", productId));

        messagingTemplate.convertAndSend(destination, (Object) message);
        log.debug("Broadcasted product {} to store: {}", action, storeId);
    }

    /**
     * Broadcast custom event
     */
    public void broadcastEvent(String storeId, String eventName, Map<String, Object> data) {
        String destination = "/topic/store/" + storeId + "/events";

        Map<String, Object> message = new HashMap<>();
        message.put("event", eventName);
        message.put("data", data);

        messagingTemplate.convertAndSend(destination, (Object) message);
        log.debug("Broadcasted event {} to store: {}", eventName, storeId);
    }

    /**
     * Send message to specific user
     */
    public void sendToUser(String username, String eventName, Map<String, Object> data) {
        String destination = "/queue/user/" + username;

        Map<String, Object> message = new HashMap<>();
        message.put("event", eventName);
        message.put("data", data);

        messagingTemplate.convertAndSendToUser(username, destination, message);
        log.debug("Sent message to user: {}", username);
    }
}
