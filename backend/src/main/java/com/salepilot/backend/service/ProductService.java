package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.entity.Product;
import com.salepilot.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer for Product management.
 * Demonstrates multi-tenant filtering via TenantContext.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Create a new product
     */
    public Product createProduct(Product product) {
        // Generate SKU if not provided
        if (product.getSku() == null || product.getSku().isEmpty()) {
            product.setSku(generateSKU());
        }

        // TenantAware entity will automatically set storeId via @PrePersist
        return productRepository.save(product);
    }

    /**
     * Update product
     */
    public Product updateProduct(Long id, Product productDetails) {
        String storeId = TenantContext.getCurrentTenant();

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Verify tenant ownership
        if (!product.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to product");
        }

        // Update fields
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setCostPrice(productDetails.getCostPrice());
        product.setStock(productDetails.getStock());
        product.setCategory(productDetails.getCategory());
        product.setSupplier(productDetails.getSupplier());

        return productRepository.save(product);
    }

    /**
     * Get product by ID
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        String storeId = TenantContext.getCurrentTenant();
        Optional<Product> product = productRepository.findById(id);

        // Verify tenant ownership
        return product.filter(p -> p.getStoreId().equals(storeId));
    }

    /**
     * Search products
     */
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String search, Pageable pageable) {
        String storeId = TenantContext.getCurrentTenant();
        return productRepository.searchProducts(storeId, search, pageable);
    }

    /**
     * Get low stock products
     */
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts() {
        String storeId = TenantContext.getCurrentTenant();
        return productRepository.findLowStockProducts(storeId);
    }

    /**
     * Find product by SKU
     */
    @Transactional(readOnly = true)
    public Optional<Product> findBySKU(String sku) {
        String storeId = TenantContext.getCurrentTenant();
        return productRepository.findByStoreIdAndSku(storeId, sku);
    }

    /**
     * Find product by barcode
     */
    @Transactional(readOnly = true)
    public Optional<Product> findByBarcode(String barcode) {
        String storeId = TenantContext.getCurrentTenant();
        return productRepository.findByStoreIdAndBarcode(storeId, barcode);
    }

    /**
     * Delete product
     */
    public void deleteProduct(Long id) {
        String storeId = TenantContext.getCurrentTenant();

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Verify tenant ownership
        if (!product.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to product");
        }

        // Soft delete by setting status to ARCHIVED
        product.setStatus(Product.ProductStatus.ARCHIVED);
        productRepository.save(product);
    }

    /**
     * Generate unique SKU
     */
    private String generateSKU() {
        return "SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
