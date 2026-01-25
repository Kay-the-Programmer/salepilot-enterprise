package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.dto.PurchaseOrderRequest;
import com.salepilot.backend.entity.*;
import com.salepilot.backend.exception.BadRequestException;
import com.salepilot.backend.exception.ConflictException;
import com.salepilot.backend.exception.NotFoundException;
import com.salepilot.backend.repository.ProductRepository;
import com.salepilot.backend.repository.PurchaseOrderItemRepository;
import com.salepilot.backend.repository.PurchaseOrderRepository;
import com.salepilot.backend.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for Purchase Order management.
 * Handles PO creation, receiving, and inventory updates.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderService {

    private final PurchaseOrderRepository poRepository;
    private final PurchaseOrderItemRepository poItemRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    /**
     * Create a new purchase order
     */
    public PurchaseOrder createPO(PurchaseOrderRequest request) {
        String storeId = TenantContext.getCurrentTenant();

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        if (!supplier.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to supplier");
        }

        PurchaseOrder po = PurchaseOrder.builder()
                .poNumber(generatePONumber(storeId))
                .supplier(supplier)
                .supplierName(supplier.getName()) // Snapshot
                .status(PurchaseOrder.POStatus.DRAFT) // Starts as DRAFT
                .expectedAt(request.getExpectedAt())
                .notes(request.getNotes())
                .shippingCost(request.getShippingCost() != null ? request.getShippingCost() : BigDecimal.ZERO)
                .tax(request.getTax() != null ? request.getTax() : BigDecimal.ZERO)
                .build();

        // Process items
        List<PurchaseOrderItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (PurchaseOrderRequest.PurchaseOrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + itemRequest.getProductId()));

            if (!product.getStoreId().equals(storeId)) {
                throw new SecurityException("Unauthorized access to product");
            }

            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .sku(product.getSku())
                    .quantity(itemRequest.getQuantity())
                    .costPrice(itemRequest.getCostPrice())
                    .receivedQuantity(BigDecimal.ZERO)
                    .build();

            items.add(item);
            subtotal = subtotal.add(item.getLineTotal());
        }

        po.setSubtotal(subtotal);
        po.setTotal(subtotal.add(po.getShippingCost()).add(po.getTax()));

        PurchaseOrder savedPO = poRepository.save(po);
        items.forEach(item -> {
            item.setPurchaseOrder(savedPO);
            poItemRepository.save(item);
        });

        return savedPO;
    }

    /**
     * Update PO status (DRAFT -> ORDERED)
     */
    public PurchaseOrder updateStatus(Long id, PurchaseOrder.POStatus status) {
        PurchaseOrder po = getPOById(id);

        if (po.getStatus() == PurchaseOrder.POStatus.RECEIVED || po.getStatus() == PurchaseOrder.POStatus.CANCELED) {
            throw new ConflictException("Cannot change status of finalized PO");
        }

        // Logic check: DRAFT -> ORDERED
        if (po.getStatus() == PurchaseOrder.POStatus.DRAFT && status == PurchaseOrder.POStatus.ORDERED) {
            po.setOrderedAt(Instant.now());
        }

        // Allow cancellation unless received
        if (status == PurchaseOrder.POStatus.CANCELED) {
            if (po.getStatus() == PurchaseOrder.POStatus.PARTIALLY_RECEIVED) {
                throw new ConflictException("Cannot cancel partially received PO");
            }
        }

        po.setStatus(status);
        return poRepository.save(po);
    }

    /**
     * Receive inventory from Purchase Order
     * 
     * @param receivedItems Map of ProductID -> Quantity Received
     */
    public PurchaseOrder receiveInventory(Long id, Map<Long, BigDecimal> receivedItems) {
        PurchaseOrder po = getPOById(id);

        if (po.getStatus() != PurchaseOrder.POStatus.ORDERED
                && po.getStatus() != PurchaseOrder.POStatus.PARTIALLY_RECEIVED) {
            throw new ConflictException("PO must be ORDERED or PARTIALLY_RECEIVED to receive inventory");
        }

        List<PurchaseOrderItem> items = poItemRepository.findByPurchaseOrder_Id(id);
        boolean fullyReceived = true;
        boolean anyReceived = false;

        for (PurchaseOrderItem item : items) {
            BigDecimal quantityToReceive = receivedItems.get(item.getProduct().getId());

            if (quantityToReceive != null && quantityToReceive.compareTo(BigDecimal.ZERO) > 0) {
                // Check if receiving more than ordered
                BigDecimal newReceivedTotal = item.getReceivedQuantity().add(quantityToReceive);
                if (newReceivedTotal.compareTo(item.getQuantity()) > 0) {
                    // In real world, over-receiving exists. Here we cap it or allow it.
                    // Let's allow it but warn or simplistic approach: validation
                    // throw new BadRequestException("Cannot receive more than ordered");
                }

                item.setReceivedQuantity(newReceivedTotal);
                poItemRepository.save(item);

                // Update Product Stock
                Product product = item.getProduct();
                product.setStock(product.getStock().add(quantityToReceive));
                // Update product cost price (Weighted Average Cost could be implemented here)
                product.setCostPrice(item.getCostPrice()); // Simple override for now
                productRepository.save(product);

                anyReceived = true;
            }

            if (!item.isFullyReceived()) {
                fullyReceived = false;
            }
        }

        if (fullyReceived) {
            po.setStatus(PurchaseOrder.POStatus.RECEIVED);
            po.setReceivedAt(Instant.now());
        } else if (anyReceived) {
            po.setStatus(PurchaseOrder.POStatus.PARTIALLY_RECEIVED);
        }

        return poRepository.save(po);
    }

    /**
     * Get PO by ID
     */
    @Transactional(readOnly = true)
    public PurchaseOrder getPOById(Long id) {
        String storeId = TenantContext.getCurrentTenant();
        PurchaseOrder po = poRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Purchase Order not found"));

        if (!po.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to PO");
        }
        return po;
    }

    /**
     * Get items for a PO
     */
    @Transactional(readOnly = true)
    public List<PurchaseOrderItem> getPOItems(Long poId) {
        getPOById(poId); // Check auth
        return poItemRepository.findByPurchaseOrder_Id(poId);
    }

    /**
     * List POs
     */
    @Transactional(readOnly = true)
    public Page<PurchaseOrder> getAllPOs(Pageable pageable) {
        String storeId = TenantContext.getCurrentTenant();
        return poRepository.findByStoreId(storeId, pageable);
    }

    private String generatePONumber(String storeId) {
        Long count = poRepository.countByStoreId(storeId);
        int year = Year.now().getValue();
        // PO-2024-0001
        return String.format("PO-%d-%04d", year, count + 1);
    }
}
