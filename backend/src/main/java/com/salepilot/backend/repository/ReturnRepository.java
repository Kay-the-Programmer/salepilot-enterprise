package com.salepilot.backend.repository;

import com.salepilot.backend.entity.Return;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Return entity.
 */
@Repository
public interface ReturnRepository extends JpaRepository<Return, Long> {

    /**
     * Find return by internal return ID
     */
    Optional<Return> findByStoreIdAndReturnId(String storeId, String returnId);

    /**
     * Find all returns for a store
     */
    Page<Return> findByStoreId(String storeId, Pageable pageable);

    /**
     * Find returns by original sale
     */
    Page<Return> findByStoreIdAndOriginalSale_Id(String storeId, Long saleId, Pageable pageable);
}
