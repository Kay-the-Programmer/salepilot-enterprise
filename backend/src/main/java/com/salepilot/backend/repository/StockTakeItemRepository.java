package com.salepilot.backend.repository;

import com.salepilot.backend.entity.StockTakeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for StockTakeItem entity.
 */
@Repository
public interface StockTakeItemRepository extends JpaRepository<StockTakeItem, Long> {

    /**
     * Find items for a specific stock take
     */
    List<StockTakeItem> findByStockTake_Id(Long stockTakeId);

    /**
     * Find specific product in active stock take
     */
    Optional<StockTakeItem> findByStockTake_IdAndProduct_Id(Long stockTakeId, Long productId);
}
