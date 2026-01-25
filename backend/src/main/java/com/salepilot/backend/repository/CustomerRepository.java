package com.salepilot.backend.repository;

import com.salepilot.backend.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Customer entity.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find all customers for a store
     */
    Page<Customer> findByStoreId(String storeId, Pageable pageable);

    /**
     * Search customers by name, email, or phone
     */
    @Query("SELECT c FROM Customer c WHERE c.storeId = :storeId AND " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "c.phone LIKE CONCAT('%', :search, '%'))")
    Page<Customer> searchCustomers(@Param("storeId") String storeId,
            @Param("search") String search,
            Pageable pageable);

    /**
     * Find customer by email within store
     */
    Optional<Customer> findByStoreIdAndEmail(String storeId, String email);

    /**
     * Find customers with outstanding balance
     */
    @Query("SELECT c FROM Customer c WHERE c.storeId = :storeId AND c.accountBalance < 0")
    List<Customer> findCustomersWithOutstandingBalance(@Param("storeId") String storeId);

    /**
     * Find customers with store credit
     */
    @Query("SELECT c FROM Customer c WHERE c.storeId = :storeId AND c.storeCredit > 0")
    List<Customer> findCustomersWithStoreCredit(@Param("storeId") String storeId);
}
