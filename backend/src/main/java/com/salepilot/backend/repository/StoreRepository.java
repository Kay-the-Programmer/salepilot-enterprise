package com.salepilot.backend.repository;

import com.salepilot.backend.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Store entity (tenant management).
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    /**
     * Find all active stores
     */
    java.util.List<Store> findByStatus(Store.StoreStatus status);

    /**
     * Find stores by subscription status
     */
    java.util.List<Store> findBySubscriptionStatus(Store.SubscriptionStatus subscriptionStatus);
}
