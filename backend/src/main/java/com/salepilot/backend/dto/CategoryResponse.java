package com.salepilot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for category data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Long id;

    private String name;

    private Long parentId;

    private String parentName;

    private String fullPath; // e.g., "Electronics > Phones > Smartphones"

    private String attributes;

    private String revenueAccountId;

    private String cogsAccountId;

    private List<CategoryResponse> subcategories; // For tree structure
}
