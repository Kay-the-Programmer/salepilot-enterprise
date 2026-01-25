package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.entity.Customer;
import com.salepilot.backend.exception.ConflictException;
import com.salepilot.backend.exception.NotFoundException;
import com.salepilot.backend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Customer management.
 * Handles customer CRUD, store credit, and A/R balance tracking.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Create a new customer
     */
    public Customer createCustomer(Customer customer) {
        String storeId = TenantContext.getCurrentTenant();

        // Check for duplicate email if provided
        if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
            Optional<Customer> existing = customerRepository.findByStoreIdAndEmail(storeId, customer.getEmail());
            if (existing.isPresent()) {
                throw new ConflictException("Customer with email '" + customer.getEmail() + "' already exists");
            }
        }

        // Initialize balances if not set
        if (customer.getStoreCredit() == null) {
            customer.setStoreCredit(BigDecimal.ZERO);
        }
        if (customer.getAccountBalance() == null) {
            customer.setAccountBalance(BigDecimal.ZERO);
        }

        // TenantAware entity will automatically set storeId via @PrePersist
        return customerRepository.save(customer);
    }

    /**
     * Update existing customer
     */
    public Customer updateCustomer(Long id, Customer customerDetails) {
        String storeId = TenantContext.getCurrentTenant();

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        // Verify tenant ownership
        if (!customer.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to customer");
        }

        // Check for email conflicts if email is being changed
        if (customerDetails.getEmail() != null && !customerDetails.getEmail().equals(customer.getEmail())) {
            Optional<Customer> existing = customerRepository.findByStoreIdAndEmail(storeId, customerDetails.getEmail());
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new ConflictException("Customer with email '" + customerDetails.getEmail() + "' already exists");
            }
        }

        // Update fields
        customer.setName(customerDetails.getName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhone(customerDetails.getPhone());
        customer.setAddress(customerDetails.getAddress());
        customer.setNotes(customerDetails.getNotes());

        // Note: storeCredit and accountBalance are updated via separate methods
        return customerRepository.save(customer);
    }

    /**
     * Get customer by ID
     */
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerById(Long id) {
        String storeId = TenantContext.getCurrentTenant();
        Optional<Customer> customer = customerRepository.findById(id);

        // Verify tenant ownership
        return customer.filter(c -> c.getStoreId().equals(storeId));
    }

    /**
     * Get all customers with pagination
     */
    @Transactional(readOnly = true)
    public Page<Customer> getAllCustomers(Pageable pageable) {
        String storeId = TenantContext.getCurrentTenant();
        return customerRepository.findByStoreId(storeId, pageable);
    }

    /**
     * Search customers by name, email, or phone
     */
    @Transactional(readOnly = true)
    public Page<Customer> searchCustomers(String search, Pageable pageable) {
        String storeId = TenantContext.getCurrentTenant();
        return customerRepository.searchCustomers(storeId, search, pageable);
    }

    /**
     * Get customers with outstanding balance (A/R)
     */
    @Transactional(readOnly = true)
    public List<Customer> getCustomersWithOutstandingBalance() {
        String storeId = TenantContext.getCurrentTenant();
        return customerRepository.findCustomersWithOutstandingBalance(storeId);
    }

    /**
     * Get customers with store credit
     */
    @Transactional(readOnly = true)
    public List<Customer> getCustomersWithStoreCredit() {
        String storeId = TenantContext.getCurrentTenant();
        return customerRepository.findCustomersWithStoreCredit(storeId);
    }

    /**
     * Add store credit to customer
     */
    public Customer addStoreCredit(Long customerId, BigDecimal amount) {
        String storeId = TenantContext.getCurrentTenant();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Store credit amount must be positive");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        // Verify tenant ownership
        if (!customer.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to customer");
        }

        customer.setStoreCredit(customer.getStoreCredit().add(amount));
        return customerRepository.save(customer);
    }

    /**
     * Deduct store credit from customer (used during sales)
     */
    public Customer deductStoreCredit(Long customerId, BigDecimal amount) {
        String storeId = TenantContext.getCurrentTenant();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deduction amount must be positive");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        // Verify tenant ownership
        if (!customer.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to customer");
        }

        // Check if sufficient credit
        if (customer.getStoreCredit().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient store credit. Available: " + customer.getStoreCredit());
        }

        customer.setStoreCredit(customer.getStoreCredit().subtract(amount));
        return customerRepository.save(customer);
    }

    /**
     * Update customer account balance (A/R)
     * Positive balance = customer has credit
     * Negative balance = customer owes money
     */
    public Customer updateAccountBalance(Long customerId, BigDecimal amount) {
        String storeId = TenantContext.getCurrentTenant();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        // Verify tenant ownership
        if (!customer.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to customer");
        }

        customer.setAccountBalance(customer.getAccountBalance().add(amount));
        return customerRepository.save(customer);
    }

    /**
     * Delete customer (soft delete)
     */
    public void deleteCustomer(Long id) {
        String storeId = TenantContext.getCurrentTenant();

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        // Verify tenant ownership
        if (!customer.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to customer");
        }

        // Check for outstanding balance
        if (customer.hasOutstandingBalance()) {
            throw new ConflictException("Cannot delete customer with outstanding balance: " +
                    customer.getOutstandingAmount());
        }

        // Check for store credit
        if (customer.getStoreCredit().compareTo(BigDecimal.ZERO) > 0) {
            throw new ConflictException("Cannot delete customer with store credit: " +
                    customer.getStoreCredit());
        }

        customerRepository.delete(customer);
    }
}
