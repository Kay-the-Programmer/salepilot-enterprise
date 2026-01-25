package com.salepilot.backend.repository;

import com.salepilot.backend.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

/**
 * Repository for Expense entity.
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Find expenses for a store
     */
    Page<Expense> findByStoreIdOrderByDateDesc(String storeId, Pageable pageable);

    /**
     * Find expenses by date range
     */
    @Query("SELECT e FROM Expense e WHERE e.storeId = :storeId AND " +
            "e.date BETWEEN :startDate AND :endDate " +
            "ORDER BY e.date DESC")
    Page<Expense> findByDateRange(@Param("storeId") String storeId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable);
}
