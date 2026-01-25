package com.salepilot.backend.repository;

import com.salepilot.backend.entity.StockTake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for StockTake entity.
 */
@Repository
public interface StockTakeRepository extends JpaRepository<StockTake, Long> {

    /**
     * Find active stock take for a store
     */
    Optional<StockTake> findByStoreIdAndStatus(String storeId, StockTake.StockTakeStatus status);

    /**
     * Find all stock takes for a store
     */
    Page<StockTake> findByStoreId(String storeId, Pageable pageable);
}
