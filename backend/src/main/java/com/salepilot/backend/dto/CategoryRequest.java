package com.salepilot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating/updating categories
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    private Long parentId; // null for root categories

    private String attributes; // JSON string for custom attributes

    private String revenueAccountId; // Accounting integration

    private String cogsAccountId; // Cost of goods sold account
}
