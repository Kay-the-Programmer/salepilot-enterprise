package com.salepilot.backend.repository;

import com.salepilot.backend.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Sale entity.
 */
@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    /**
     * Find sale by transaction ID
     */
    Optional<Sale> findByStoreIdAndTransactionId(String storeId, String transactionId);

    /**
     * Find all sales for a store
     */
    Page<Sale> findByStoreId(String storeId, Pageable pageable);

    /**
     * Find sales by date range
     */
    @Query("SELECT s FROM Sale s WHERE s.storeId = :storeId AND " +
            "s.timestamp BETWEEN :startDate AND :endDate " +
            "ORDER BY s.timestamp DESC")
    Page<Sale> findByDateRange(@Param("storeId") String storeId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable);

    /**
     * Find sales by customer
     */
    Page<Sale> findByStoreIdAndCustomer_Id(String storeId, Long customerId, Pageable pageable);

    /**
     * Find sales by payment status
     */
    Page<Sale> findByStoreIdAndPaymentStatus(String storeId, Sale.PaymentStatus paymentStatus, Pageable pageable);

    /**
     * Calculate total sales for date range
     */
    @Query("SELECT SUM(s.total) FROM Sale s WHERE s.storeId = :storeId AND " +
            "s.timestamp BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalSales(@Param("storeId") String storeId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);

    /**
     * Count sales for date range
     */
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.storeId = :storeId AND " +
            "s.timestamp BETWEEN :startDate AND :endDate")
    Long countSalesByDateRange(@Param("storeId") String storeId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);
}
