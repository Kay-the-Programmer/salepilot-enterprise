package com.salepilot.backend.repository;

import com.salepilot.backend.entity.JournalEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository for JournalEntry entity.
 */
@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    /**
     * Find all journal entries for a store
     */
    Page<JournalEntry> findByStoreIdOrderByDateDesc(String storeId, Pageable pageable);

    /**
     * Find journal entries by date range
     */
    @Query("SELECT je FROM JournalEntry je WHERE je.storeId = :storeId AND " +
            "je.date BETWEEN :startDate AND :endDate " +
            "ORDER BY je.date DESC")
    Page<JournalEntry> findByDateRange(@Param("storeId") String storeId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable);

    /**
     * Find journal entries by source
     */
    List<JournalEntry> findByStoreIdAndSourceTypeAndSourceId(String storeId,
            JournalEntry.SourceType sourceType,
            String sourceId);
}
