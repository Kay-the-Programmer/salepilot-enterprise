package com.salepilot.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when insufficient stock is available for a transaction
 */
public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(String productName, double requested, double available) {
        super(String.format("Insufficient stock for product '%s'. Requested: %.2f, Available: %.2f",
                productName, requested, available),
                HttpStatus.BAD_REQUEST,
                "INSUFFICIENT_STOCK");
    }
}
