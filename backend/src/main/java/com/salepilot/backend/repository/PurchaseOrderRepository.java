package com.salepilot.backend.repository;

import com.salepilot.backend.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for PurchaseOrder entity.
 */
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    /**
     * Find PO by PO number
     */
    Optional<PurchaseOrder> findByStoreIdAndPoNumber(String storeId, String poNumber);

    /**
     * Find all POs for a store
     */
    Page<PurchaseOrder> findByStoreId(String storeId, Pageable pageable);

    /**
     * Find POs by status
     */
    Page<PurchaseOrder> findByStoreIdAndStatus(String storeId, PurchaseOrder.POStatus status, Pageable pageable);

    /**
     * Find POs by supplier
     */
    Page<PurchaseOrder> findByStoreIdAndSupplier_Id(String storeId, Long supplierId, Pageable pageable);

    /**
     * Generate next PO number for store
     */
    @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.storeId = :storeId")
    Long countByStoreId(@Param("storeId") String storeId);
}
