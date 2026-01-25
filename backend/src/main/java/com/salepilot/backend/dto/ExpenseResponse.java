package com.salepilot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response DTO for Expense details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {

    private Long id;
    private Instant date;
    private String description;
    private BigDecimal amount;
    private Long expenseAccountId;
    private String expenseAccountName;
    private Long paymentAccountId;
    private String paymentAccountName;
    private String category;
    private String reference;
    private String createdBy;
}
