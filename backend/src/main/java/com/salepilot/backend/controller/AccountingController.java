package com.salepilot.backend.controller;

import com.salepilot.backend.dto.AccountDTO;
import com.salepilot.backend.dto.JournalEntryDTO;
import com.salepilot.backend.dto.TrialBalanceDTO;
import com.salepilot.backend.entity.Account;
import com.salepilot.backend.entity.JournalEntry;
import com.salepilot.backend.service.AccountingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Accounting Module.
 */
@RestController
@RequestMapping("/api/v1/accounting")
@RequiredArgsConstructor
@Tag(name = "Accounting", description = "Chart of Accounts and General Ledger endpoints")
public class AccountingController {

    private final AccountingService accountingService;

    @PostMapping("/accounts")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @Operation(summary = "Create new account in Chart of Accounts")
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO request) {
        Account account = accountingService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapAccountToDTO(account));
    }

    @GetMapping("/accounts")
    @Operation(summary = "Get Chart of Accounts")
    public ResponseEntity<List<AccountDTO>> getChartOfAccounts() {
        List<Account> accounts = accountingService.getAllAccounts();
        return ResponseEntity.ok(accounts.stream()
                .map(this::mapAccountToDTO)
                .collect(Collectors.toList()));
    }

    @PostMapping("/journal-entries")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @Operation(summary = "Post manual journal entry")
    public ResponseEntity<Void> postJournalEntry(@Valid @RequestBody JournalEntryDTO request) {
        accountingService.postJournalEntry(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/reports/trial-balance")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ACCOUNTANT')")
    @Operation(summary = "Get Trial Balance Report")
    public ResponseEntity<TrialBalanceDTO> getTrialBalance() {
        return ResponseEntity.ok(accountingService.getTrialBalance());
    }

    // Mapper Helper
    private AccountDTO mapAccountToDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .name(account.getName())
                .number(account.getNumber())
                .type(account.getType())
                .subType(account.getSubType())
                .balance(account.getBalance())
                .description(account.getDescription())
                .isDebitNormal(account.getIsDebitNormal())
                .build();
    }
}
