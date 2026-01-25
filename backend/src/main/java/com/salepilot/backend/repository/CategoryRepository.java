package com.salepilot.backend.repository;

import com.salepilot.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Category entity.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find all categories for a store
     */
    List<Category> findByStoreId(String storeId);

    /**
     * Find root categories (no parent) for a store
     */
    List<Category> findByStoreIdAndParentIsNull(String storeId);

    /**
     * Find subcategories of a parent category
     */
    List<Category> findByStoreIdAndParent_Id(String storeId, Long parentId);

    /**
     * Find category by name within store
     */
    Optional<Category> findByStoreIdAndName(String storeId, String name);
}
