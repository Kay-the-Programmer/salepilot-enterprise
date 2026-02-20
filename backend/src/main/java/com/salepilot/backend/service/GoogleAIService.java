package com.salepilot.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.salepilot.backend.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

/**
 * Service for Google Generative AI (Gemini) integration via REST API.
 * Uses WebClient to avoid dependency resolution issues with the client library.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAIService {

    private final AppProperties appProperties;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    /**
     * Generate product description using AI
     */
    public String generateProductDescription(String productName, String category) {
        if (!isAvailable()) {
            return "";
        }

        String prompt = String.format(
                "You are an expert copywriter for an e-commerce store.\n" +
                        "Generate a compelling, short (2-3 sentences) product description for a product with the following details:\n"
                        +
                        "- Product Name: \"%s\"\n" +
                        "- Category: \"%s\"\n" +
                        "The description should be engaging, highlight key benefits, and be suitable for a product listing.\n"
                        +
                        "Do not include the product name or category in the description itself.",
                productName, category);

        return callGeminiApi(prompt);
    }

    /**
     * Generate AI chat response with business context
     */
    public String generateChatResponse(String prompt) {
        if (!isAvailable()) {
            return "AI service is not configured.";
        }
        return callGeminiApi(prompt);
    }

    private String callGeminiApi(String textInfo) {
        try {
            String apiKey = appProperties.getAi().getGoogleApiKey();
            String model = appProperties.getAi().getModel(); // e.g. gemini-pro

            // Build JSON Body
            // { "contents": [{ "parts": [{ "text": "..." }] }] }
            ObjectNode root = objectMapper.createObjectNode();
            ArrayNode contents = root.putArray("contents");
            ObjectNode content = contents.addObject();
            ArrayNode parts = content.putArray("parts");
            parts.addObject().put("text", textInfo);

            String url = String.format(GEMINI_API_URL, model, apiKey);

            String responseBody = webClientBuilder.build()
                    .post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(root.toPrettyString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Parse Response
            // { "candidates": [ { "content": { "parts": [ { "text": "..." } ] } } ] }
            JsonNode responseJson = objectMapper.readTree(responseBody);
            JsonNode candidates = responseJson.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode partsNode = firstCandidate.path("content").path("parts");
                if (partsNode.isArray() && partsNode.size() > 0) {
                    return partsNode.get(0).path("text").asText().trim();
                }
            }

            return "No response generated.";

        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage());
            return "Error generating AI response.";
        }
    }

    public boolean isAvailable() {
        return appProperties.getAi().getGoogleApiKey() != null &&
                !appProperties.getAi().getGoogleApiKey().isEmpty();
    }
}
