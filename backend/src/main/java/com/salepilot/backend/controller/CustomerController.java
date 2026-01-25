package com.salepilot.backend.controller;

import com.salepilot.backend.dto.CustomerRequest;
import com.salepilot.backend.dto.CustomerResponse;
import com.salepilot.backend.entity.Customer;
import com.salepilot.backend.service.CustomerService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Customer management.
 * Handles customer CRUD, store credit, and A/R balance.
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer management endpoints")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Create new customer")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        Customer customer = mapToEntity(request);
        Customer created = customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(customer -> ResponseEntity.ok(mapToResponse(customer)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Update customer")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {
        Customer customer = mapToEntity(request);
        Customer updated = customerService.updateCustomer(id, customer);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete customer")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all customers with pagination")
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers(Pageable pageable) {
        Page<Customer> customers = customerService.getAllCustomers(pageable);
        return ResponseEntity.ok(customers.map(this::mapToResponse));
    }

    @GetMapping("/search")
    @Operation(summary = "Search customers by name, email, or phone")
    public ResponseEntity<Page<CustomerResponse>> searchCustomers(
            @RequestParam String query,
            Pageable pageable) {
        Page<Customer> customers = customerService.searchCustomers(query, pageable);
        return ResponseEntity.ok(customers.map(this::mapToResponse));
    }

    @GetMapping("/outstanding-balance")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get customers with outstanding A/R balance")
    public ResponseEntity<List<CustomerResponse>> getCustomersWithOutstandingBalance() {
        List<Customer> customers = customerService.getCustomersWithOutstandingBalance();
        return ResponseEntity.ok(customers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/store-credit")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get customers with store credit")
    public ResponseEntity<List<CustomerResponse>> getCustomersWithStoreCredit() {
        List<Customer> customers = customerService.getCustomersWithStoreCredit();
        return ResponseEntity.ok(customers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    @PostMapping("/{id}/store-credit")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Add store credit to customer")
    public ResponseEntity<CustomerResponse> addStoreCredit(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        Customer updated = customerService.addStoreCredit(id, amount);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @PostMapping("/{id}/account-balance")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update customer A/R account balance")
    public ResponseEntity<CustomerResponse> updateAccountBalance(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        Customer updated = customerService.updateAccountBalance(id, amount);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    // DTO Mapping

    private Customer mapToEntity(CustomerRequest request) {
        return Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .notes(request.getNotes())
                .build();
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .notes(customer.getNotes())
                .createdAt(customer.getCreatedAt())
                .storeCredit(customer.getStoreCredit())
                .accountBalance(customer.getAccountBalance())
                .hasOutstandingBalance(customer.hasOutstandingBalance())
                .outstandingAmount(customer.getOutstandingAmount())
                .build();
    }
}
