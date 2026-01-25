package com.salepilot.backend.repository;

import com.salepilot.backend.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Page<Supplier> findByStoreId(String storeId, Pageable pageable);

    Optional<Supplier> findByStoreIdAndName(String storeId, String name);

    @Query("SELECT s FROM Supplier s WHERE s.storeId = :storeId AND " +
            "(LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Supplier> searchSuppliers(@Param("storeId") String storeId,
            @Param("search") String search,
            Pageable pageable);
}
