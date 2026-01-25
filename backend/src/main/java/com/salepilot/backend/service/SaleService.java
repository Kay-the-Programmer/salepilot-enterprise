package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.dto.SaleRequest;
import com.salepilot.backend.entity.*;
import com.salepilot.backend.exception.BadRequestException;
import com.salepilot.backend.exception.NotFoundException;
import com.salepilot.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for Sale and transaction management.
 * Handles sale processing, stock deduction, payment recording, and customer
 * updates.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerService customerService;

    /**
     * Create a new sale transaction
     */
    public Sale createSale(SaleRequest request) {
        String storeId = TenantContext.getCurrentTenant();

        // 1. Process Sale Items & Calculate Totals
        List<SaleItem> saleItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (SaleRequest.SaleItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + itemRequest.getProductId()));

            if (!product.getStoreId().equals(storeId)) {
                throw new SecurityException("Unauthorized access to product");
            }

            // Check stock (simplified check, handling backorders could be added)
            // if (product.getStock().compareTo(itemRequest.getQuantity()) < 0) {
            // throw new BadRequestException("Insufficient stock for product: " +
            // product.getName());
            // }

            // Deduct stock
            product.setStock(product.getStock().subtract(itemRequest.getQuantity()));
            productRepository.save(product);

            SaleItem item = SaleItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .priceAtSale(itemRequest.getPrice())
                    .costAtSale(product.getCostPrice())
                    .build();

            saleItems.add(item);
            subtotal = subtotal.add(itemRequest.getPrice().multiply(itemRequest.getQuantity()));
        }

        // 2. Determine Totals
        BigDecimal discount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;
        BigDecimal tax = request.getTax() != null ? request.getTax() : BigDecimal.ZERO;
        BigDecimal total = subtotal.subtract(discount).add(tax);

        // 3. Handle Store Credit
        BigDecimal storeCreditUsed = BigDecimal.ZERO;
        if (request.getStoreCreditUsed() != null && request.getStoreCreditUsed().compareTo(BigDecimal.ZERO) > 0) {
            if (request.getCustomerId() == null) {
                throw new BadRequestException("Customer is required to use store credit");
            }
            // Use CustomerService to deduct and validate credit
            customerService.deductStoreCredit(request.getCustomerId(), request.getStoreCreditUsed());
            storeCreditUsed = request.getStoreCreditUsed();
        }

        // 4. Handle Customer & A/R
        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new NotFoundException("Customer not found"));
            if (!customer.getStoreId().equals(storeId)) {
                throw new SecurityException("Auth error on customer");
            }
        }

        BigDecimal amountPaid = request.getAmountPaid() != null ? request.getAmountPaid() : BigDecimal.ZERO;
        BigDecimal totalPaid = amountPaid.add(storeCreditUsed);
        BigDecimal balanceDue = total.subtract(totalPaid);

        Sale.PaymentStatus paymentStatus;
        if (balanceDue.compareTo(BigDecimal.ZERO) <= 0) {
            paymentStatus = Sale.PaymentStatus.PAID;
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            paymentStatus = Sale.PaymentStatus.PARTIALLY_PAID;
        } else {
            paymentStatus = Sale.PaymentStatus.UNPAID;
        }

        // Update customer balance if unpaid (A/R)
        if (balanceDue.compareTo(BigDecimal.ZERO) > 0 && customer != null) {
            // Negative balance means customer owes money
            customerService.updateAccountBalance(customer.getId(), balanceDue.negate());
        }

        // 5. Build Sale Entity
        Sale sale = Sale.builder()
                .transactionId(generateTransactionId())
                .timestamp(Instant.now())
                .customer(customer)
                .subtotal(subtotal)
                .discount(discount)
                .tax(tax)
                .total(total)
                .storeCreditUsed(storeCreditUsed)
                .amountPaid(totalPaid) // Includes store credit
                .paymentStatus(paymentStatus)
                .fulfillmentStatus(Sale.FulfillmentStatus.FULFILLED) // Default for POS
                .channel(request.getChannel() != null ? request.getChannel() : Sale.SalesChannel.POS)
                .dueDate(request.getDueDate())
                .customerDetails(customer != null ? buildCustomerDetails(customer) : null)
                .build();

        final Sale savedSale = saleRepository.save(sale);

        // 6. Save Sale Items
        saleItems.forEach(item -> {
            item.setSale(savedSale);
            saleItemRepository.save(item);
        });

        // 7. Record Payment
        if (amountPaid.compareTo(BigDecimal.ZERO) > 0) {
            Payment payment = Payment.builder()
                    .paymentId(UUID.randomUUID().toString())
                    .sale(savedSale)
                    .amount(amountPaid)
                    .method(request.getPaymentMethod())
                    .reference(request.getPaymentReference())
                    .date(Instant.now())
                    .build();
            paymentRepository.save(payment);
        }

        return savedSale;
    }

    /**
     * Get sale by ID
     */
    @Transactional(readOnly = true)
    public Sale getSaleById(Long id) {
        String storeId = TenantContext.getCurrentTenant();
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sale not found"));

        if (!sale.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access to sale");
        }
        return sale;
    }

    /**
     * List sales with pagination
     */
    @Transactional(readOnly = true)
    public Page<Sale> getAllSales(Pageable pageable) {
        String storeId = TenantContext.getCurrentTenant();
        return saleRepository.findByStoreId(storeId, pageable);
    }

    /**
     * Get items for a sale
     */
    @Transactional(readOnly = true)
    public List<SaleItem> getSaleItems(Long saleId) {
        String storeId = TenantContext.getCurrentTenant();
        // Check sale ownership first (optimization: can join, but safe is check sale
        // first)
        getSaleById(saleId);
        return saleItemRepository.findBySale_Id(saleId);
    }

    /**
     * Get payments for a sale
     */
    @Transactional(readOnly = true)
    public List<Payment> getSalePayments(Long saleId) {
        // check ownership via getSaleById
        getSaleById(saleId);
        String storeId = TenantContext.getCurrentTenant();
        return paymentRepository.findByStoreIdAndSale_Id(storeId, saleId);
    }

    /**
     * Add payment to existing sale
     */
    public Payment addPayment(Long saleId, BigDecimal amount, String method, String reference) {
        Sale sale = getSaleById(saleId);

        // Validate amount (don't overpay beyond reason, logic depends on business
        // rules)
        // Here we just accept payment.

        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID().toString())
                .sale(sale)
                .amount(amount)
                .method(method)
                .reference(reference)
                .date(Instant.now())
                .build();

        paymentRepository.save(payment);

        // Update Sale totals
        BigDecimal totalPaidBefore = sale.getAmountPaid();
        BigDecimal newTotalPaid = totalPaidBefore.add(amount);
        sale.setAmountPaid(newTotalPaid);

        // Update Customer Balance (reduce debt)
        if (sale.getCustomer() != null) {
            customerService.updateAccountBalance(sale.getCustomer().getId(), amount);
        }

        // Update Status
        if (newTotalPaid.compareTo(sale.getTotal()) >= 0) {
            sale.setPaymentStatus(Sale.PaymentStatus.PAID);
        } else {
            sale.setPaymentStatus(Sale.PaymentStatus.PARTIALLY_PAID);
        }

        saleRepository.save(sale);
        return payment;
    }

    private String generateTransactionId() {
        return "TRX-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private String buildCustomerDetails(Customer c) {
        // Simple JSON-like string or use ObjectMapper if available
        return String.format("{\"name\": \"%s\", \"email\": \"%s\", \"id\": \"%s\"}",
                c.getName(), c.getEmail() != null ? c.getEmail() : "", c.getId());
    }
}
