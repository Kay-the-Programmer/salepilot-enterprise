package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.dto.StockTakeResponse;
import com.salepilot.backend.entity.*;
import com.salepilot.backend.exception.BadRequestException;
import com.salepilot.backend.exception.ConflictException;
import com.salepilot.backend.exception.NotFoundException;
import com.salepilot.backend.repository.ProductRepository;
import com.salepilot.backend.repository.StockTakeItemRepository;
import com.salepilot.backend.repository.StockTakeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Service layer for Stock Take / Inventory Counting.
 * Handles starting sessions, recording counts, and finalizing adjustments.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StockTakeService {

    private final StockTakeRepository stockTakeRepository;
    private final StockTakeItemRepository stockTakeItemRepository;
    private final ProductRepository productRepository;

    /**
     * Start a new stock take session
     * Snapshots current stock levels as "Expected"
     */
    public StockTake startStockTake() {
        String storeId = TenantContext.getCurrentTenant();

        // Check if active session exists
        if (stockTakeRepository.findByStoreIdAndStatus(storeId, StockTake.StockTakeStatus.ACTIVE).isPresent()) {
            throw new ConflictException("An active stock take session already exists");
        }

        StockTake stockTake = StockTake.builder()
                .status(StockTake.StockTakeStatus.ACTIVE)
                .startTime(Instant.now())
                .build();

        StockTake savedStockTake = stockTakeRepository.save(stockTake);

        // Snapshot all active products (simplified: fetch all pageable or list)
        // Note: For large inventory, this should be batched or async
        List<Product> products = productRepository.findAll(); // Optimization needed for large DB

        for (Product product : products) {
            // Filter by current store (redundant if repo is filtered, but safe)
            if (product.getStoreId().equals(storeId) && product.getStatus() == Product.ProductStatus.ACTIVE) {
                StockTakeItem item = StockTakeItem.builder()
                        .stockTake(savedStockTake)
                        .product(product)
                        .name(product.getName())
                        .sku(product.getSku())
                        .expected(product.getStock())
                        .counted(null) // Not counted yet
                        .build();
                stockTakeItemRepository.save(item);
            }
        }

        return savedStockTake;
    }

    /**
     * Get active stock take session
     */
    @Transactional(readOnly = true)
    public StockTake getActiveStockTake() {
        String storeId = TenantContext.getCurrentTenant();
        return stockTakeRepository.findByStoreIdAndStatus(storeId, StockTake.StockTakeStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("No active stock take session found"));
    }

    /**
     * Update counted quantity for an item
     */
    public StockTakeItem updateItemCount(Long productId, BigDecimal quantity) {
        StockTake activeSession = getActiveStockTake();

        StockTakeItem item = stockTakeItemRepository.findByStockTake_IdAndProduct_Id(activeSession.getId(), productId)
                .orElseThrow(() -> new NotFoundException("Product not found in active stock take"));

        item.setCounted(quantity);
        return stockTakeItemRepository.save(item);
    }

    /**
     * Finalize stock take and apply adjustments
     */
    public StockTake finalizeStockTake() {
        StockTake activeSession = getActiveStockTake();
        List<StockTakeItem> items = stockTakeItemRepository.findByStockTake_Id(activeSession.getId());

        for (StockTakeItem item : items) {
            // If counted is set, update actual inventory
            if (item.getCounted() != null && !item.getCounted().equals(item.getExpected())) {
                Product product = item.getProduct();
                product.setStock(item.getCounted());
                productRepository.save(product);

                // TODO: Generate Inventory Adjustment record (for history/accounting)
            }
        }

        activeSession.complete();
        return stockTakeRepository.save(activeSession);
    }

    /**
     * Get items for a stock take
     */
    @Transactional(readOnly = true)
    public List<StockTakeItem> getStockTakeItems(Long stockTakeId) {
        String storeId = TenantContext.getCurrentTenant();
        StockTake st = stockTakeRepository.findById(stockTakeId)
                .orElseThrow(() -> new NotFoundException("Stock take not found"));

        if (!st.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access");
        }

        return stockTakeItemRepository.findByStockTake_Id(stockTakeId);
    }

    /**
     * List past stock takes
     */
    @Transactional(readOnly = true)
    public Page<StockTake> getStockTakeHistory(Pageable pageable) {
        String storeId = TenantContext.getCurrentTenant();
        return stockTakeRepository.findByStoreId(storeId, pageable);
    }
}
