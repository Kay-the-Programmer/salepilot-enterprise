package com.salepilot.backend.repository;

import com.salepilot.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Product entity.
 * Note: Multi-tenant filtering should be applied at service layer via
 * TenantContext.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

        /**
         * Find product by SKU within tenant
         */
        Optional<Product> findByStoreIdAndSku(String storeId, String sku);

        /**
         * Find product by barcode within tenant
         */
        Optional<Product> findByStoreIdAndBarcode(String storeId, String barcode);

        /**
         * Find all products by store ID and status
         */
        Page<Product> findByStoreIdAndStatus(String storeId, Product.ProductStatus status, Pageable pageable);

        /**
         * Search products by name or SKU
         */
        @Query("SELECT p FROM Product p WHERE p.storeId = :storeId AND " +
                        "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%')))")
        Page<Product> searchProducts(@Param("storeId") String storeId,
                        @Param("search") String search,
                        Pageable pageable);

        /**
         * Find low stock products (stock <= reorderPoint)
         */
        @Query("SELECT p FROM Product p WHERE p.storeId = :storeId AND " +
                        "p.status = 'ACTIVE' AND p.reorderPoint IS NOT NULL AND " +
                        "p.stock <= p.reorderPoint")
        List<Product> findLowStockProducts(@Param("storeId") String storeId);

        /**
         * Find products by category
         */
        Page<Product> findByStoreIdAndCategory_Id(String storeId, Long categoryId, Pageable pageable);

        /**
         * Find products by supplier
         */
        Page<Product> findByStoreIdAndSupplier_Id(String storeId, Long supplierId, Pageable pageable);

        /**
         * Find all products by store ID
         */
        Page<Product> findByStoreId(String storeId, Pageable pageable);
}
