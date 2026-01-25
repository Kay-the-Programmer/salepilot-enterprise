package com.salepilot.backend.dto;

import com.salepilot.backend.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Trial Balance Report
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrialBalanceDTO {

    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private boolean isBalanced;
    private List<AccountBalanceDTO> accounts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountBalanceDTO {
        private Long accountId;
        private String accountNumber;
        private String accountName;
        private Account.AccountType type;
        private BigDecimal debit;
        private BigDecimal credit;
        private BigDecimal netBalance;
    }
}
