package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.dto.ReturnRequest;
import com.salepilot.backend.entity.*;
import com.salepilot.backend.exception.BadRequestException;
import com.salepilot.backend.exception.NotFoundException;
import com.salepilot.backend.repository.ProductRepository;
import com.salepilot.backend.repository.ReturnItemRepository;
import com.salepilot.backend.repository.ReturnRepository;
import com.salepilot.backend.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for Return management.
 * Handles refund processing, stock adjustments, and sale updates.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final ReturnItemRepository returnItemRepository;
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CustomerService customerService; // For store credit refunds

    /**
     * Create a new return/refund
     */
    public Return createReturn(ReturnRequest request) {
        String storeId = TenantContext.getCurrentTenant();

        Sale originalSale = saleRepository.findById(request.getSaleId())
                .orElseThrow(() -> new NotFoundException("Original sale not found"));

        if (!originalSale.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to sale");
        }

        // Validate refund amount (cannot exceed sale total)
        // Note: Simple validation, real logic should check if already refunded
        if (request.getRefundAmount().compareTo(originalSale.getAmountPaid()) > 0) {
            throw new BadRequestException("Refund amount cannot exceed amount paid");
        }

        Return returnRecord = Return.builder()
                .returnId(UUID.randomUUID().toString())
                .originalSale(originalSale)
                .timestamp(Instant.now())
                .refundAmount(request.getRefundAmount())
                .refundMethod(request.getRefundMethod())
                .build();

        // Process return items
        List<ReturnItem> items = new ArrayList<>();

        for (ReturnRequest.ReturnItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + itemRequest.getProductId()));

            // Check product belongs to store
            if (!product.getStoreId().equals(storeId)) {
                throw new SecurityException("Unauthorized access to product");
            }

            ReturnItem item = ReturnItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .quantity(itemRequest.getQuantity())
                    .reason(itemRequest.getReason())
                    .addToStock(itemRequest.isAddToStock())
                    .build();

            items.add(item);

            // Handle Stock Adjustment
            if (itemRequest.isAddToStock()) {
                product.setStock(product.getStock().add(itemRequest.getQuantity()));
                productRepository.save(product);
            }
        }

        // Save Return
        Return savedReturn = returnRepository.save(returnRecord);
        items.forEach(item -> {
            item.setReturnRecord(savedReturn);
            returnItemRepository.save(item);
        });

        // Update Sale Status
        // Simplistic logic: if refund > 0, mark partial or full
        if (request.getRefundAmount().compareTo(originalSale.getTotal()) >= 0) {
            originalSale.setRefundStatus(Sale.RefundStatus.FULLY_REFUNDED);
        } else {
            originalSale.setRefundStatus(Sale.RefundStatus.PARTIALLY_REFUNDED);
        }
        saleRepository.save(originalSale);

        // Handle Store Credit Refund
        if ("Store Credit".equalsIgnoreCase(request.getRefundMethod()) && originalSale.getCustomer() != null) {
            customerService.addStoreCredit(originalSale.getCustomer().getId(), request.getRefundAmount());
        }

        return savedReturn;
    }

    /**
     * Get Return by ID
     */
    @Transactional(readOnly = true)
    public Return getReturnById(Long id) {
        String storeId = TenantContext.getCurrentTenant();
        Return returnRecord = returnRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Return not found"));

        if (!returnRecord.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to return");
        }
        return returnRecord;
    }

    /**
     * Get items for a return
     */
    @Transactional(readOnly = true)
    public List<ReturnItem> getReturnItems(Long returnId) {
        getReturnById(returnId); // Auth check
        return returnItemRepository.findByReturnRecord_Id(returnId);
    }

    /**
     * Get all returns
     */
    @Transactional(readOnly = true)
    public Page<Return> getAllReturns(Pageable pageable) {
        String storeId = TenantContext.getCurrentTenant();
        return returnRepository.findByStoreId(storeId, pageable);
    }
}
