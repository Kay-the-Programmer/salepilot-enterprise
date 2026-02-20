package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.dto.ChatRequest;
import com.salepilot.backend.dto.ChatResponse;
import com.salepilot.backend.dto.DashboardMetricsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.stream.Collectors;

/**
 * Service for AI Chat integration (Google Gemini).
 * Implements intent recognition and context gathering to provide business
 * intelligence.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIChatService {

        private final ReportService reportService;
        private final GoogleAIService googleAIService;

        public ChatResponse processMessage(ChatRequest request) {
                String query = request.getMessage();
                String storeId = TenantContext.getCurrentTenant();

                // 1. Analyze Intent
                Intent intent = analyzeIntent(query);

                // 2. Gather Context
                String context = gatherContext(storeId, intent);

                // 3. Construct System Prompt
                String systemPrompt = buildSystemPrompt(context, query);

                // 4. Generate Response
                String aiResponse = googleAIService.generateChatResponse(systemPrompt);

                return ChatResponse.builder()
                                .response(aiResponse)
                                .build();
        }

        private Intent analyzeIntent(String query) {
                String lowerQuery = query.toLowerCase();
                Intent intent = new Intent();

                if (lowerQuery.contains("strategy") || lowerQuery.contains("grow") || lowerQuery.contains("improve")) {
                        intent.isStrategy = true;
                }
                if (lowerQuery.contains("sales") || lowerQuery.contains("revenue") || lowerQuery.contains("overview")) {
                        intent.needsSales = true;
                }
                if (lowerQuery.contains("stock") || lowerQuery.contains("inventory")) {
                        intent.needsInventory = true;
                }

                // Default to overview if nothing specific
                if (!intent.isStrategy && !intent.needsSales && !intent.needsInventory) {
                        intent.needsSales = true;
                        intent.needsInventory = true;
                }

                return intent;
        }

        private String gatherContext(String storeId, Intent intent) {
                StringBuilder sb = new StringBuilder();
                DashboardMetricsDTO metrics = reportService.getDashboardMetrics();

                if (intent.needsSales || intent.isStrategy) {
                        sb.append("SALES DATA:\n");
                        sb.append("- Daily Sales: $").append(metrics.getDailySales()).append("\n");
                        sb.append("- Transaction Count: ").append(metrics.getDailyTransactionCount()).append("\n");
                        sb.append("- Monthly Sales: $").append(metrics.getMonthlySales()).append("\n");

                        if (metrics.getTopProducts() != null && !metrics.getTopProducts().isEmpty()) {
                                String topProds = metrics.getTopProducts().stream()
                                                .map(p -> p.getName() + " (" + p.getQuantitySold() + " sold)")
                                                .collect(Collectors.joining(", "));
                                sb.append("- Top Products (Month): ").append(topProds).append("\n");
                        }
                        sb.append("\n");
                }

                if (intent.needsInventory || intent.isStrategy) {
                        sb.append("INVENTORY DATA:\n");
                        sb.append("- Low Stock Items: ").append(metrics.getLowStockCount()).append("\n");
                        sb.append("- Out of Stock Items: ").append(metrics.getOutOfStockCount()).append("\n");
                        sb.append("\n");
                }

                return sb.toString();
        }

        private String buildSystemPrompt(String context, String userQuery) {
                return String.format("""
                                You are "Salepilot Assistant", an intelligent business intelligence assistant.

                                Current Date: %s

                                BUSINESS DATA:
                                %s

                                USER QUESTION: "%s"

                                INSTRUCTIONS:
                                - Provide a clear, conversational answer using the data above.
                                - Use specific numbers and metrics from the data.
                                - Be professional but helpful.
                                - If data is missing (e.g. 0 sales), acknowledge it gently.
                                """, LocalDate.now(), context, userQuery);
        }

        private static class Intent {
                boolean isStrategy;
                boolean needsSales;
                boolean needsInventory;
        }
}
