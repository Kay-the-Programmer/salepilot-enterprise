package com.salepilot.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a SKU already exists
 */
public class DuplicateSKUException extends BusinessException {
    public DuplicateSKUException(String sku) {
        super(String.format("Product with SKU '%s' already exists in this store", sku),
                HttpStatus.CONFLICT,
                "DUPLICATE_SKU");
    }
}
