package com.salepilot.backend.controller;

import com.salepilot.backend.dto.ProductResponse;
import com.salepilot.backend.dto.SaleRequest;
import com.salepilot.backend.dto.SaleResponse;
import com.salepilot.backend.entity.Product;
import com.salepilot.backend.entity.Sale;
import com.salepilot.backend.service.ProductService;
import com.salepilot.backend.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * Public REST Controller for Online Shop Storefront.
 * Access rules should be relaxed here (Public/Guest).
 */
@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
@Tag(name = "Online Shop", description = "Public storefront endpoints")
public class OnlineShopController {

    private final ProductService productService;
    private final SaleService saleService;

    // TODO: Create a way to identify Tenant for public requests (Header or
    // Subdomain)
    // For now, assuming TenantFilter handles it or we pass it.

    @GetMapping("/products")
    @Operation(summary = "List public products")
    public ResponseEntity<Page<ProductResponse>> listProducts(
            @RequestParam(required = false) String category,
            Pageable pageable) {
        // Implementation note: ProductService methods currently use TenantContext.
        // Public API must ensure TenantContext is set (via Interceptor looking at
        // 'X-Tenant-ID' or host)

        Page<Product> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products.map(this::mapToProductResponse));
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "Get product details")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(mapToProductResponse(product));
    }

    @PostMapping("/checkout")
    @Operation(summary = "Place an online order")
    public ResponseEntity<SaleResponse> placeOrder(@Valid @RequestBody SaleRequest request) {
        // Enforce channel
        request.setChannel(Sale.SalesChannel.ONLINE);
        // Note: validation should support guest customers (customerId null)

        Sale sale = saleService.createSale(request);
        return ResponseEntity.ok(mapToSaleResponse(sale));
    }

    // Mappers (Simplified duplicated from other controllers, ideally move to shared
    // Mapper)
    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .imageUrl(product.getImageUrl())
                // .description() if available
                .build();
    }

    private SaleResponse mapToSaleResponse(Sale sale) {
        // Only return basic confirmation info
        return SaleResponse.builder()
                .id(sale.getId())
                .transactionId(sale.getTransactionId())
                .total(sale.getTotal())
                .paymentStatus(sale.getPaymentStatus())
                .build();
    }
}
