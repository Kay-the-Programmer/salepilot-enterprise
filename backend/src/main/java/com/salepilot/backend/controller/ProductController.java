package com.salepilot.backend.controller;

import com.salepilot.backend.dto.ProductRequest;
import com.salepilot.backend.dto.ProductResponse;
import com.salepilot.backend.entity.Product;
import com.salepilot.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Product management.
 * Demonstrates API layer with DTO mapping and security.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create new product")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        Product product = mapToEntity(request);
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(mapToResponse(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update product")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequest request) {
        Product product = mapToEntity(request);
        Product updated = productService.updateProduct(id, product);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete product (archive)")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search products")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam String query,
            Pageable pageable) {
        Page<Product> products = productService.searchProducts(query, pageable);
        return ResponseEntity.ok(products.map(this::mapToResponse));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock products")
    public ResponseEntity<List<ProductResponse>> getLowStockProducts() {
        List<Product> products = productService.getLowStockProducts();
        return ResponseEntity.ok(products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Find product by SKU")
    public ResponseEntity<ProductResponse> findBySKU(@PathVariable String sku) {
        return productService.findBySKU(sku)
                .map(product -> ResponseEntity.ok(mapToResponse(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/barcode/{barcode}")
    @Operation(summary = "Find product by barcode")
    public ResponseEntity<ProductResponse> findByBarcode(@PathVariable String barcode) {
        return productService.findByBarcode(barcode)
                .map(product -> ResponseEntity.ok(mapToResponse(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    // DTO Mapping

    private Product mapToEntity(ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .sku(request.getSku())
                .barcode(request.getBarcode())
                .price(request.getPrice())
                .costPrice(request.getCostPrice())
                .stock(request.getStock())
                .imageUrls(request.getImageUrls())
                .brand(request.getBrand())
                .reorderPoint(request.getReorderPoint())
                .weight(request.getWeight())
                .dimensions(request.getDimensions())
                .safetyStock(request.getSafetyStock())
                .build();
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .sku(product.getSku())
                .barcode(product.getBarcode())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .supplierName(product.getSupplier() != null ? product.getSupplier().getName() : null)
                .price(product.getPrice())
                .costPrice(product.getCostPrice())
                .stock(product.getStock())
                .unitOfMeasure(product.getUnitOfMeasure().name())
                .imageUrls(product.getImageUrls() != null ? java.util.Arrays.asList(product.getImageUrls())
                        : java.util.Collections.emptyList())
                .status(product.getStatus().name())
                .lowStock(product.isLowStock())
                .profitMargin(product.getProfitMargin())
                .build();
    }
}
