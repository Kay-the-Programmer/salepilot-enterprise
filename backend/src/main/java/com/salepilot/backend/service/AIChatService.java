package com.salepilot.backend.service;

import com.salepilot.backend.dto.ChatRequest;
import com.salepilot.backend.dto.ChatResponse;
import com.salepilot.backend.dto.DashboardMetricsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for AI Chat integration (Google Gemini).
 * Currently implements heuristic responses based on system data.
 */
@Service
@RequiredArgsConstructor
public class AIChatService {

    private final ReportService reportService;

    // In real implementation, inject Gemini Client here
    // @Value("${gemini.api.key}")
    // private String apiKey;

    public ChatResponse processMessage(ChatRequest request) {
        String msg = request.getMessage().toLowerCase();

        // Simple Intent Recognition for Demo purposes
        if (msg.contains("sales") && (msg.contains("today") || msg.contains("daily"))) {
            DashboardMetricsDTO metrics = reportService.getDashboardMetrics();
            return ChatResponse.builder()
                    .response("Your total sales today are " + metrics.getDailySales() + " across "
                            + metrics.getDailyTransactionCount() + " transactions.")
                    .build();
        }

        if (msg.contains("stock") || msg.contains("inventory")) {
            DashboardMetricsDTO metrics = reportService.getDashboardMetrics();
            return ChatResponse.builder()
                    .response("You have " + metrics.getLowStockCount() + " low stock items and "
                            + metrics.getOutOfStockCount() + " items out of stock.")
                    .action("NAVIGATE_TO_INVENTORY")
                    .build();
        }

        if (msg.contains("hello") || msg.contains("hi")) {
            return ChatResponse.builder()
                    .response("Hello! I'm your SalePilot AI assistant. Ask me about your sales, inventory, or reports.")
                    .build();
        }

        return ChatResponse.builder()
                .response("I'm not sure about that yet. I can help with sales data and inventory status.")
                .build();
    }
}
