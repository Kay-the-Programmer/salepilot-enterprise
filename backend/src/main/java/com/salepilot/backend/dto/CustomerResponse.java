package com.salepilot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response DTO for customer data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private String address; // JSON string

    private String notes;

    private Instant createdAt;

    private BigDecimal storeCredit;

    private BigDecimal accountBalance;

    private boolean hasOutstandingBalance;

    private BigDecimal outstandingAmount;
}
