package com.salepilot.backend.repository;

import com.salepilot.backend.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Repository for SaleItem entity.
 */
@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    /**
     * Find all items for a sale
     */
    List<SaleItem> findBySale_Id(Long saleId);

    /**
     * Find items by product
     */
    List<SaleItem> findByStoreIdAndProduct_Id(String storeId, Long productId);

    /**
     * Get top selling products by quantity for date range
     */
    @Query("SELECT si.product.id, si.product.name, SUM(si.quantity) as totalQty FROM SaleItem si " +
            "WHERE si.storeId = :storeId AND si.sale.timestamp BETWEEN :startDate AND :endDate " +
            "GROUP BY si.product.id, si.product.name " +
            "ORDER BY totalQty DESC")
    List<Object[]> findTopSellingProducts(@Param("storeId") String storeId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            org.springframework.data.domain.Pageable pageable);
}
