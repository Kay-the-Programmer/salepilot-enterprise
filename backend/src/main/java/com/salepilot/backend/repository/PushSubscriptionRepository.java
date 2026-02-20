package com.salepilot.backend.repository;

import com.salepilot.backend.entity.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PushSubscription entity.
 */
@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {

    /**
     * Find subscriptions for a user
     */
    List<PushSubscription> findByUserId(Long userId);

    /**
     * Find specific endpoint (to avoid duplicates)
     */
    Optional<PushSubscription> findByEndpoint(String endpoint);

    /**
     * Delete by endpoint (unsubscribe)
     */
    void deleteByEndpoint(String endpoint);
}
