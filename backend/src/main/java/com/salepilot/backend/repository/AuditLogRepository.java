package com.salepilot.backend.repository;

import com.salepilot.backend.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for AuditLog entity.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Find all audit logs for a store
     */
    Page<AuditLog> findByStoreIdOrderByTimestampDesc(String storeId, Pageable pageable);

    /**
     * Find audit logs by user
     */
    Page<AuditLog> findByStoreIdAndUser_IdOrderByTimestampDesc(String storeId, Long userId, Pageable pageable);

    /**
     * Find audit logs by action type
     */
    Page<AuditLog> findByStoreIdAndActionOrderByTimestampDesc(String storeId, String action, Pageable pageable);
}
