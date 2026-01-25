package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.entity.Category;
import com.salepilot.backend.exception.ConflictException;
import com.salepilot.backend.exception.NotFoundException;
import com.salepilot.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Category management.
 * Handles hierarchical category structure with parent-child relationships.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Create a new category
     */
    public Category createCategory(Category category) {
        String storeId = TenantContext.getCurrentTenant();

        // Check for duplicate name within store
        Optional<Category> existing = categoryRepository.findByStoreIdAndName(storeId, category.getName());
        if (existing.isPresent()) {
            throw new ConflictException("Category with name '" + category.getName() + "' already exists");
        }

        // Validate parent if specified
        if (category.getParent() != null) {
            Category parent = categoryRepository.findById(category.getParent().getId())
                    .orElseThrow(() -> new NotFoundException("Parent category not found"));

            // Verify parent belongs to same store
            if (!parent.getStoreId().equals(storeId)) {
                throw new SecurityException("Parent category belongs to different store");
            }
        }

        // TenantAware entity will automatically set storeId via @PrePersist
        return categoryRepository.save(category);
    }

    /**
     * Update existing category
     */
    public Category updateCategory(Long id, Category categoryDetails) {
        String storeId = TenantContext.getCurrentTenant();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        // Verify tenant ownership
        if (!category.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to category");
        }

        // Check for name conflicts (excluding current category)
        Optional<Category> existing = categoryRepository.findByStoreIdAndName(storeId, categoryDetails.getName());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new ConflictException("Category with name '" + categoryDetails.getName() + "' already exists");
        }

        // Update fields
        category.setName(categoryDetails.getName());
        category.setAttributes(categoryDetails.getAttributes());
        category.setRevenueAccountId(categoryDetails.getRevenueAccountId());
        category.setCogsAccountId(categoryDetails.getCogsAccountId());

        // Update parent if changed
        if (categoryDetails.getParent() != null) {
            // Prevent circular references
            if (categoryDetails.getParent().getId().equals(id)) {
                throw new ConflictException("Category cannot be its own parent");
            }

            Category parent = categoryRepository.findById(categoryDetails.getParent().getId())
                    .orElseThrow(() -> new NotFoundException("Parent category not found"));

            if (!parent.getStoreId().equals(storeId)) {
                throw new SecurityException("Parent category belongs to different store");
            }

            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return categoryRepository.save(category);
    }

    /**
     * Get category by ID
     */
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        String storeId = TenantContext.getCurrentTenant();
        Optional<Category> category = categoryRepository.findById(id);

        // Verify tenant ownership
        return category.filter(c -> c.getStoreId().equals(storeId));
    }

    /**
     * Get all categories for current store
     */
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        String storeId = TenantContext.getCurrentTenant();
        return categoryRepository.findByStoreId(storeId);
    }

    /**
     * Get root categories (no parent) for current store
     */
    @Transactional(readOnly = true)
    public List<Category> getRootCategories() {
        String storeId = TenantContext.getCurrentTenant();
        return categoryRepository.findByStoreIdAndParentIsNull(storeId);
    }

    /**
     * Get subcategories of a specific parent
     */
    @Transactional(readOnly = true)
    public List<Category> getSubcategories(Long parentId) {
        String storeId = TenantContext.getCurrentTenant();

        // Verify parent exists and belongs to store
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Parent category not found"));

        if (!parent.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to category");
        }

        return categoryRepository.findByStoreIdAndParent_Id(storeId, parentId);
    }

    /**
     * Delete category (only if no products are assigned)
     */
    public void deleteCategory(Long id) {
        String storeId = TenantContext.getCurrentTenant();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        // Verify tenant ownership
        if (!category.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to category");
        }

        // Check if category has subcategories
        List<Category> subcategories = categoryRepository.findByStoreIdAndParent_Id(storeId, id);
        if (!subcategories.isEmpty()) {
            throw new ConflictException(
                    "Cannot delete category with subcategories. Delete or reassign subcategories first.");
        }

        // TODO: Check if category has products assigned (requires ProductRepository)
        // For now, we'll proceed with deletion

        categoryRepository.delete(category);
    }
}
