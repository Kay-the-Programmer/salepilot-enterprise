package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.dto.ExpenseRequest;
import com.salepilot.backend.entity.Account;
import com.salepilot.backend.entity.Expense;
import com.salepilot.backend.exception.NotFoundException;
import com.salepilot.backend.repository.AccountRepository;
import com.salepilot.backend.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Service layer for Expense management.
 * Handles expense creation and integration with accounting.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final AccountRepository accountRepository;
    // In future: integrate with AccountingService for auto-posting Journal Entries

    /**
     * Create a new expense
     */
    public Expense createExpense(ExpenseRequest request) {
        String storeId = TenantContext.getCurrentTenant();

        Account expenseAccount = accountRepository.findById(request.getExpenseAccountId())
                .orElseThrow(() -> new NotFoundException("Expense account not found"));

        Account paymentAccount = accountRepository.findById(request.getPaymentAccountId())
                .orElseThrow(() -> new NotFoundException("Payment account not found"));

        if (!expenseAccount.getStoreId().equals(storeId) || !paymentAccount.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to account");
        }

        // Validate generic types if we want to enforce structure
        // e.g. expenseAccount.getType() == EXPENSE

        Expense expense = Expense.builder()
                .date(request.getDate() != null ? request.getDate() : Instant.now())
                .description(request.getDescription())
                .amount(request.getAmount())
                .expenseAccount(expenseAccount)
                .expenseAccountName(expenseAccount.getName())
                .paymentAccount(paymentAccount)
                .paymentAccountName(paymentAccount.getName())
                .category(request.getCategory())
                .reference(request.getReference())
                .createdBy("SYSTEM") // TODO: Get from security context
                .build();

        // TODO: Post Journal Entry automatically via AccountingService
        // Debit: Expense Account
        // Credit: Payment Account (Cash/Bank)

        return expenseRepository.save(expense);
    }

    /**
     * Get expense by ID
     */
    @Transactional(readOnly = true)
    public Expense getExpenseById(Long id) {
        String storeId = TenantContext.getCurrentTenant();
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Expense not found"));

        if (!expense.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to expense");
        }
        return expense;
    }

    /**
     * List expenses
     */
    @Transactional(readOnly = true)
    public Page<Expense> getAllExpenses(Pageable pageable) {
        String storeId = TenantContext.getCurrentTenant();
        return expenseRepository.findByStoreIdOrderByDateDesc(storeId, pageable);
    }
}
