package com.salepilot.backend.dto;

import com.salepilot.backend.entity.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Account management
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {

    private Long id;

    @NotBlank(message = "Account name is required")
    private String name;

    @NotBlank(message = "Account number is required")
    private String number;

    @NotNull(message = "Account type is required")
    private Account.AccountType type;

    private Account.AccountSubType subType;

    private BigDecimal balance; // Read-only

    private String description;

    @NotNull(message = "Debit normal flag is required")
    private Boolean isDebitNormal;
}
