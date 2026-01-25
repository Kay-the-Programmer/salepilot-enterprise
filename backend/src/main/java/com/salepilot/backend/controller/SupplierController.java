package com.salepilot.backend.controller;

import com.salepilot.backend.dto.SupplierRequest;
import com.salepilot.backend.dto.SupplierResponse;
import com.salepilot.backend.entity.Supplier;
import com.salepilot.backend.service.SupplierService;
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
 * REST Controller for Supplier management.
 * Handles supplier CRUD for purchase order management.
 */
@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
@Tag(name = "Suppliers", description = "Supplier management endpoints")
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Create new supplier")
    public ResponseEntity<SupplierResponse> createSupplier(@Valid @RequestBody SupplierRequest request) {
        Supplier supplier = mapToEntity(request);
        Supplier created = supplierService.createSupplier(supplier);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID")
    public ResponseEntity<SupplierResponse> getSupplier(@PathVariable Long id) {
        return supplierService.getSupplierById(id)
                .map(supplier -> ResponseEntity.ok(mapToResponse(supplier)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Update supplier")
    public ResponseEntity<SupplierResponse> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request) {
        Supplier supplier = mapToEntity(request);
        Supplier updated = supplierService.updateSupplier(id, supplier);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete supplier")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all suppliers with pagination")
    public ResponseEntity<Page<SupplierResponse>> getAllSuppliers(Pageable pageable) {
        Page<Supplier> suppliers = supplierService.getAllSuppliers(pageable);
        return ResponseEntity.ok(suppliers.map(this::mapToResponse));
    }

    @GetMapping("/search")
    @Operation(summary = "Search suppliers by name or email")
    public ResponseEntity<Page<SupplierResponse>> searchSuppliers(
            @RequestParam String query,
            Pageable pageable) {
        Page<Supplier> suppliers = supplierService.searchSuppliers(query, pageable);
        return ResponseEntity.ok(suppliers.map(this::mapToResponse));
    }

    // DTO Mapping

    private Supplier mapToEntity(SupplierRequest request) {
        return Supplier.builder()
                .name(request.getName())
                .contactPerson(request.getContactPerson())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .paymentTerms(request.getPaymentTerms())
                .bankingDetails(request.getBankingDetails())
                .notes(request.getNotes())
                .build();
    }

    private SupplierResponse mapToResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .contactPerson(supplier.getContactPerson())
                .phone(supplier.getPhone())
                .email(supplier.getEmail())
                .address(supplier.getAddress())
                .paymentTerms(supplier.getPaymentTerms())
                .bankingDetails(supplier.getBankingDetails())
                .notes(supplier.getNotes())
                .build();
    }
}
