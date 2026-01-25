package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.dto.DashboardMetricsDTO;
import com.salepilot.backend.entity.Product;
import com.salepilot.backend.repository.ProductRepository;
import com.salepilot.backend.repository.SaleItemRepository;
import com.salepilot.backend.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Reports and Dashboard analytics.
 * Aggregates data from various repositories.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;

    /**
     * Get key metrics for the dashboard
     */
    public DashboardMetricsDTO getDashboardMetrics() {
        String storeId = TenantContext.getCurrentTenant();

        // Date Ranges
        Instant now = Instant.now();
        Instant startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        // 1. Sales Metrics
        BigDecimal dailySales = saleRepository.calculateTotalSales(storeId, startOfDay, now);
        if (dailySales == null)
            dailySales = BigDecimal.ZERO;

        Long dailyCount = saleRepository.countSalesByDateRange(storeId, startOfDay, now);

        BigDecimal monthlySales = saleRepository.calculateTotalSales(storeId, startOfMonth, now);
        if (monthlySales == null)
            monthlySales = BigDecimal.ZERO;

        // 2. Inventory Metrics
        // Simple counts. Ideally we'd have dedicated counts in repo
        List<Product> lowStockProducts = productRepository.findLowStockProducts(storeId);
        Long lowStockCount = (long) lowStockProducts.size();

        // Out of stock (stock <= 0)
        // Note: Ideally create a specific query for count to avoid fetching entities
        Long outOfStockCount = productRepository.findAll().stream()
                .filter(p -> p.getStoreId().equals(storeId) && p.getStock().compareTo(BigDecimal.ZERO) <= 0)
                .count();

        // 3. Top Products (This Month)
        List<Object[]> topItems = saleItemRepository.findTopSellingProducts(
                storeId, startOfMonth, now, PageRequest.of(0, 5));

        List<DashboardMetricsDTO.TopProductDTO> topProducts = topItems.stream()
                .map(row -> DashboardMetricsDTO.TopProductDTO.builder()
                        .name((String) row[1]) // Index 1 is name
                        .quantitySold((BigDecimal) row[2]) // Index 2 is totalQty
                        .revenue(BigDecimal.ZERO) // Todo: Calc revenue in query
                        .build())
                .collect(Collectors.toList());

        return DashboardMetricsDTO.builder()
                .dailySales(dailySales)
                .dailyTransactionCount(dailyCount)
                .monthlySales(monthlySales)
                .lowStockCount(lowStockCount)
                .outOfStockCount(outOfStockCount)
                .topProducts(topProducts)
                .build();
    }
}
