package com.salepilot.backend.dto;

import com.salepilot.backend.entity.Sale;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for creating a new sale transaction
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleRequest {

    private Long customerId;

    @NotEmpty(message = "Sale must have at least one item")
    @Valid
    private List<SaleItemRequest> items;

    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    private BigDecimal discount;

    @DecimalMin(value = "0.0", message = "Tax cannot be negative")
    private BigDecimal tax;

    @DecimalMin(value = "0.0", message = "Amount paid cannot be negative")
    private BigDecimal amountPaid;

    private String paymentMethod; // Used if amountPaid > 0

    private String paymentReference;

    private BigDecimal storeCreditUsed;

    private Sale.SalesChannel channel;

    private LocalDate dueDate; // For invoices

    private String notes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @DecimalMin(value = "0.001", message = "Quantity must be greater than zero")
        private BigDecimal quantity;

        @DecimalMin(value = "0.0", message = "Price cannot be negative")
        private BigDecimal price; // Override price allowed
    }
}
