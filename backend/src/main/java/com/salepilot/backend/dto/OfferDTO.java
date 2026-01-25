package com.salepilot.backend.dto;

import com.salepilot.backend.entity.Offer;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for Offer interactions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferDTO {

    private Long id;
    private String userId;
    private String userName;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Latitude is required")
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private BigDecimal longitude;

    private Offer.OfferStatus status;
    private Instant createdAt;

    // For acceptance
    private String acceptedByUserId;
    private String acceptedByUserName;
}
