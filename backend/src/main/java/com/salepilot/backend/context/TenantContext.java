package com.salepilot.backend.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread-local context holder for the current tenant (store) ID.
 * This enables automatic tenant filtering throughout the application.
 * 
 * Usage:
 * - Set tenant ID after JWT authentication:
 * TenantContext.setCurrentTenant(storeId)
 * - Get current tenant: TenantContext.getCurrentTenant()
 * - Clear after request: TenantContext.clear()
 */
public class TenantContext {

    private static final Logger logger = LoggerFactory.getLogger(TenantContext.class);

    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    /**
     * Sets the current tenant ID for this thread
     * 
     * @param tenantId The store ID to set as current tenant
     */
    public static void setCurrentTenant(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            logger.warn("Attempted to set null or empty tenant ID");
            return;
        }
        logger.debug("Setting tenant context to: {}", tenantId);
        currentTenant.set(tenantId);
    }

    /**
     * Gets the current tenant ID for this thread
     * 
     * @return The current store ID, or null if not set
     */
    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    /**
     * Clears the current tenant ID from this thread.
     * Should be called after request processing to prevent memory leaks.
     */
    public static void clear() {
        String tenantId = currentTenant.get();
        if (tenantId != null) {
            logger.debug("Clearing tenant context for: {}", tenantId);
        }
        currentTenant.remove();
    }

    /**
     * Checks if a tenant is currently set
     * 
     * @return true if tenant is set, false otherwise
     */
    public static boolean isSet() {
        return currentTenant.get() != null;
    }
}
