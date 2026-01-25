package com.salepilot.backend.repository;

import com.salepilot.backend.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for PurchaseOrderItem entity.
 */
@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {

    /**
     * Find items for a specific purchase order
     */
    List<PurchaseOrderItem> findByPurchaseOrder_Id(Long purchaseOrderId);
}
