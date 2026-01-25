package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.entity.Supplier;
import com.salepilot.backend.exception.ConflictException;
import com.salepilot.backend.exception.NotFoundException;
import com.salepilot.backend.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service layer for Supplier management.
 * Handles supplier CRUD operations for purchase order management.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SupplierService {

    private final SupplierRepository supplierRepository;

    /**
     * Create a new supplier
     */
    public Supplier createSupplier(Supplier supplier) {
        String storeId = TenantContext.getCurrentTenant();

        // Check for duplicate name within store
        Optional<Supplier> existing = supplierRepository.findByStoreIdAndName(storeId, supplier.getName());
        if (existing.isPresent()) {
            throw new ConflictException("Supplier with name '" + supplier.getName() + "' already exists");
        }

        // TenantAware entity will automatically set storeId via @PrePersist
        return supplierRepository.save(supplier);
    }

    /**
     * Update existing supplier
     */
    public Supplier updateSupplier(Long id, Supplier supplierDetails) {
        String storeId = TenantContext.getCurrentTenant();

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        // Verify tenant ownership
        if (!supplier.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to supplier");
        }

        // Check for name conflicts (excluding current supplier)
        Optional<Supplier> existing = supplierRepository.findByStoreIdAndName(storeId, supplierDetails.getName());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new ConflictException("Supplier with name '" + supplierDetails.getName() + "' already exists");
        }

        // Update fields
        supplier.setName(supplierDetails.getName());
        supplier.setContactPerson(supplierDetails.getContactPerson());
        supplier.setPhone(supplierDetails.getPhone());
        supplier.setEmail(supplierDetails.getEmail());
        supplier.setAddress(supplierDetails.getAddress());
        supplier.setPaymentTerms(supplierDetails.getPaymentTerms());
        supplier.setBankingDetails(supplierDetails.getBankingDetails());
        supplier.setNotes(supplierDetails.getNotes());

        return supplierRepository.save(supplier);
    }

    /**
     * Get supplier by ID
     */
    @Transactional(readOnly = true)
    public Optional<Supplier> getSupplierById(Long id) {
        String storeId = TenantContext.getCurrentTenant();
        Optional<Supplier> supplier = supplierRepository.findById(id);

        // Verify tenant ownership
        return supplier.filter(s -> s.getStoreId().equals(storeId));
    }

    /**
     * Get all suppliers with pagination
     */
    @Transactional(readOnly = true)
    public Page<Supplier> getAllSuppliers(Pageable pageable) {
        String storeId = TenantContext.getCurrentTenant();
        return supplierRepository.findByStoreId(storeId, pageable);
    }

    /**
     * Search suppliers by name or email
     */
    @Transactional(readOnly = true)
    public Page<Supplier> searchSuppliers(String search, Pageable pageable) {
        String storeId = TenantContext.getCurrentTenant();
        return supplierRepository.searchSuppliers(storeId, search, pageable);
    }

    /**
     * Delete supplier
     */
    public void deleteSupplier(Long id) {
        String storeId = TenantContext.getCurrentTenant();

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        // Verify tenant ownership
        if (!supplier.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to supplier");
        }

        // TODO: Check if supplier has active purchase orders or products
        // For now, we'll proceed with deletion

        supplierRepository.delete(supplier);
    }
}
