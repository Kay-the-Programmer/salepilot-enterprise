package com.salepilot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for Product details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private String sku;
    private String barcode;
    private String categoryName;
    private String supplierName;
    private BigDecimal price;
    private BigDecimal costPrice;
    private BigDecimal stock;
    private String unitOfMeasure;
    private List<String> imageUrls;

    // Additional fields needed for Online Shop
    private String imageUrl; // Primary image

    // Status fields
    private String status;
    private boolean lowStock;
    private BigDecimal profitMargin;

    public String getImageUrl() {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            return imageUrls.get(0);
        }
        return imageUrl;
    }
}
