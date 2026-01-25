package com.salepilot.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import com.salepilot.backend.context.TenantContext;

/**
 * Base class for all tenant-aware entities.
 * Automatically sets the storeId before persist and update operations.
 */
@MappedSuperclass
public abstract class TenantAware extends BaseEntity {

    @Column(name = "store_id", nullable = false, updatable = false)
    private String storeId;

    /**
     * Automatically set the tenant ID before persisting
     */
    @PrePersist
    protected void prePersist() {
        if (this.storeId == null) {
            String currentTenant = TenantContext.getCurrentTenant();
            if (currentTenant != null) {
                this.storeId = currentTenant;
            } else {
                throw new IllegalStateException(
                        "Cannot persist tenant-aware entity without tenant context set");
            }
        }
    }

    /**
     * Ensure tenant ID doesn't change on update
     */
    @PreUpdate
    protected void preUpdate() {
        String currentTenant = TenantContext.getCurrentTenant();
        if (currentTenant != null && !currentTenant.equals(this.storeId)) {
            throw new IllegalStateException(
                    "Cannot change tenant ID of existing entity");
        }
    }

    // Getters and Setters

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
