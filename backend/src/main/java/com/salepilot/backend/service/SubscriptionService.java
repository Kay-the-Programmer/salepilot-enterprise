package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.entity.Store;
import com.salepilot.backend.entity.SubscriptionPlan;
import com.salepilot.backend.exception.NotFoundException;
import com.salepilot.backend.repository.StoreRepository;
import com.salepilot.backend.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service for Subscription and Billing.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionPlanRepository planRepository;
    private final StoreRepository storeRepository;

    /**
     * Get active plans
     */
    @Transactional(readOnly = true)
    public List<SubscriptionPlan> getActivePlans() {
        return planRepository.findByIsActiveTrue();
    }

    /**
     * Subscribe store to a plan (Stub implementation)
     */
    public void subscribe(String planCode) {
        String storeId = TenantContext.getCurrentTenant();

        Store store = storeRepository.findById(Long.parseLong(storeId))
                .orElseThrow(() -> new NotFoundException("Store not found"));

        SubscriptionPlan plan = planRepository.findByCode(planCode)
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        // In real app: Process Payment (Stripe) here

        // Update Store
        store.setSubscriptionStatus(Store.SubscriptionStatus.ACTIVE);

        // Simulating monthly
        store.setSubscriptionEndsAt(java.time.LocalDateTime.now().plus(30, java.time.temporal.ChronoUnit.DAYS));

        // We might want to store which plan they are on.
        // Store entity might need 'plan_id' field.
        // For now, assuming just status update.

        storeRepository.save(store);
    }

    /**
     * Initialize Default Plans
     */
    public void initPlans() {
        if (planRepository.count() == 0) {
            // Create Free, Standard, Pro
        }
    }
}
