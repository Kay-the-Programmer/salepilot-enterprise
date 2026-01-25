package com.salepilot.backend.controller;

import com.salepilot.backend.dto.ExpenseRequest;
import com.salepilot.backend.dto.ExpenseResponse;
import com.salepilot.backend.entity.Expense;
import com.salepilot.backend.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Expense management.
 */
@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Business expense management endpoints")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create new expense")
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest request) {
        Expense expense = expenseService.createExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(expense));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expense details")
    public ResponseEntity<ExpenseResponse> getExpense(@PathVariable Long id) {
        Expense expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(mapToResponse(expense));
    }

    @GetMapping
    @Operation(summary = "List expenses")
    public ResponseEntity<Page<ExpenseResponse>> listExpenses(Pageable pageable) {
        Page<Expense> expenses = expenseService.getAllExpenses(pageable);
        return ResponseEntity.ok(expenses.map(this::mapToResponse));
    }

    // Mapper helper
    private ExpenseResponse mapToResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .date(expense.getDate())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .expenseAccountId(expense.getExpenseAccount().getId())
                .expenseAccountName(expense.getExpenseAccountName())
                .paymentAccountId(expense.getPaymentAccount().getId())
                .paymentAccountName(expense.getPaymentAccountName())
                .category(expense.getCategory())
                .reference(expense.getReference())
                .createdBy(expense.getCreatedBy())
                .build();
    }
}
