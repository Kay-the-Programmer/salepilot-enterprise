package com.salepilot.backend.controller;

import com.salepilot.backend.dto.SaleRequest;
import com.salepilot.backend.dto.SaleResponse;
import com.salepilot.backend.entity.Payment;
import com.salepilot.backend.entity.Sale;
import com.salepilot.backend.entity.SaleItem;
import com.salepilot.backend.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Sales & POS management.
 */
@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Sales and POS transaction endpoints")
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Create new sale transaction (POS)")
    public ResponseEntity<SaleResponse> createSale(@Valid @RequestBody SaleRequest request) {
        Sale sale = saleService.createSale(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(sale));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sale details by ID")
    public ResponseEntity<SaleResponse> getSale(@PathVariable Long id) {
        Sale sale = saleService.getSaleById(id);
        return ResponseEntity.ok(mapToResponse(sale));
    }

    @GetMapping
    @Operation(summary = "Get all sales with pagination")
    public ResponseEntity<Page<SaleResponse>> getAllSales(Pageable pageable) {
        Page<Sale> sales = saleService.getAllSales(pageable);
        return ResponseEntity.ok(sales.map(this::mapToResponse));
    }

    @PostMapping("/{id}/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Add payment to existing sale")
    public ResponseEntity<SaleResponse> addPayment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> paymentRequest) {

        BigDecimal amount = new BigDecimal(paymentRequest.get("amount").toString());
        String method = (String) paymentRequest.get("method");
        String reference = (String) paymentRequest.getOrDefault("reference", "");

        saleService.addPayment(id, amount, method, reference);
        // Return updated sale
        return ResponseEntity.ok(mapToResponse(saleService.getSaleById(id)));
    }

    // Mapper helper
    private SaleResponse mapToResponse(Sale sale) {
        List<SaleItem> items = saleService.getSaleItems(sale.getId());
        List<Payment> payments = saleService.getSalePayments(sale.getId());

        List<SaleResponse.SaleItemResponse> itemResponses = items.stream()
                .map(item -> SaleResponse.SaleItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .sku(item.getProduct().getSku())
                        .quantity(item.getQuantity())
                        .price(item.getPriceAtSale())
                        .total(item.getLineTotal())
                        .build())
                .collect(Collectors.toList());

        List<SaleResponse.PaymentResponse> paymentResponses = payments.stream()
                .map(pay -> SaleResponse.PaymentResponse.builder()
                        .id(pay.getPaymentId())
                        .date(pay.getDate())
                        .amount(pay.getAmount())
                        .method(pay.getMethod())
                        .reference(pay.getReference())
                        .build())
                .collect(Collectors.toList());

        BigDecimal balanceDue = sale.getTotal().subtract(sale.getAmountPaid());
        if (balanceDue.compareTo(BigDecimal.ZERO) < 0)
            balanceDue = BigDecimal.ZERO;

        return SaleResponse.builder()
                .id(sale.getId())
                .transactionId(sale.getTransactionId())
                .timestamp(sale.getTimestamp())
                .customerId(sale.getCustomer() != null ? sale.getCustomer().getId() : null)
                .customerName(sale.getCustomer() != null ? sale.getCustomer().getName() : null)
                .total(sale.getTotal())
                .subtotal(sale.getSubtotal())
                .tax(sale.getTax())
                .discount(sale.getDiscount())
                .storeCreditUsed(sale.getStoreCreditUsed())
                .paymentStatus(sale.getPaymentStatus())
                .fulfillmentStatus(sale.getFulfillmentStatus())
                .channel(sale.getChannel())
                .amountPaid(sale.getAmountPaid())
                .balanceDue(balanceDue)
                .dueDate(sale.getDueDate())
                .refundStatus(sale.getRefundStatus())
                .items(itemResponses)
                .payments(paymentResponses)
                .build();
    }
}
