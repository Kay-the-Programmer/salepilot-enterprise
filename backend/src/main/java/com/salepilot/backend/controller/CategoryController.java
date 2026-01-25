package com.salepilot.backend.controller;

import com.salepilot.backend.dto.CategoryRequest;
import com.salepilot.backend.dto.CategoryResponse;
import com.salepilot.backend.entity.Category;
import com.salepilot.backend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Category management.
 * Handles hierarchical category structure.
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create new category")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        Category category = mapToEntity(request);
        Category created = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(category -> ResponseEntity.ok(mapToResponse(category)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update category")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        Category category = mapToEntity(request);
        Category updated = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete category")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/roots")
    @Operation(summary = "Get root categories (no parent)")
    public ResponseEntity<List<CategoryResponse>> getRootCategories() {
        List<Category> categories = categoryService.getRootCategories();
        return ResponseEntity.ok(categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{parentId}/subcategories")
    @Operation(summary = "Get subcategories of a parent category")
    public ResponseEntity<List<CategoryResponse>> getSubcategories(@PathVariable Long parentId) {
        List<Category> subcategories = categoryService.getSubcategories(parentId);
        return ResponseEntity.ok(subcategories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    // DTO Mapping

    private Category mapToEntity(CategoryRequest request) {
        Category.CategoryBuilder builder = Category.builder()
                .name(request.getName())
                .attributes(request.getAttributes())
                .revenueAccountId(request.getRevenueAccountId())
                .cogsAccountId(request.getCogsAccountId());

        // Set parent if provided
        if (request.getParentId() != null) {
            Category parent = new Category();
            parent.setId(request.getParentId());
            builder.parent(parent);
        }

        return builder.build();
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .fullPath(category.getFullPath())
                .attributes(category.getAttributes())
                .revenueAccountId(category.getRevenueAccountId())
                .cogsAccountId(category.getCogsAccountId())
                .build();
    }
}
