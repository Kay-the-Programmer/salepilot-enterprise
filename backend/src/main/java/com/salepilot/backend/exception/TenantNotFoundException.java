package com.salepilot.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when tenant (store) is not found or not accessible
 */
public class TenantNotFoundException extends BusinessException {
    public TenantNotFoundException(String storeId) {
        super(String.format("Store with ID '%s' not found or not accessible", storeId),
                HttpStatus.NOT_FOUND,
                "TENANT_NOT_FOUND");
    }

    public TenantNotFoundException(String message, HttpStatus status) {
        super(message, status, "TENANT_NOT_FOUND");
    }
}
