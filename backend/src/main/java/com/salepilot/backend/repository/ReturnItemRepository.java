package com.salepilot.backend.repository;

import com.salepilot.backend.entity.ReturnItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ReturnItem entity.
 */
@Repository
public interface ReturnItemRepository extends JpaRepository<ReturnItem, Long> {

    /**
     * Find items for a specific return
     */
    List<ReturnItem> findByReturnRecord_Id(Long returnId);
}
