package com.salepilot.backend.repository;

import com.salepilot.backend.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Payment entity.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payment by internal payment ID
     */
    Optional<Payment> findByStoreIdAndPaymentId(String storeId, String paymentId);

    /**
     * Find payments for a specific sale
     */
    List<Payment> findByStoreIdAndSale_Id(String storeId, Long saleId);

    /**
     * Find all payments for a store
     */
    Page<Payment> findByStoreId(String storeId, Pageable pageable);
}
