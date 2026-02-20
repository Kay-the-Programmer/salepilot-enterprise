package com.salepilot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTOs for Product API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private String description;
    private String sku;
    private String barcode;
    private Long categoryId;
    private Long supplierId;
    private BigDecimal price;
    private BigDecimal costPrice;
    private BigDecimal stock;
    private String unitOfMeasure; // "UNIT" or "KG"
    private String[] imageUrls;
    private String brand;
    private Integer reorderPoint;
    private BigDecimal weight;
    private String dimensions;
    private Integer safetyStock;
}
