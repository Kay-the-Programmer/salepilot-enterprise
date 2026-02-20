package com.salepilot.backend.service;

import com.salepilot.backend.dto.AdminStoreDTO;
import com.salepilot.backend.entity.Store;
import com.salepilot.backend.exception.NotFoundException;
import com.salepilot.backend.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for Superadmin operations.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SuperadminService {

    private final StoreRepository storeRepository;

    /**
     * List all stores (with optional status filter)
     */
    @Transactional(readOnly = true)
    public Page<AdminStoreDTO> getAllStores(Pageable pageable) {
        return storeRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    /**
     * Suspend/Unsuspend store
     */
    public void updateStoreStatus(Long storeId, Store.StoreStatus status) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("Store not found"));

        store.setStatus(status);
        storeRepository.save(store);
    }

    /**
     * Verify store manually
     */
    public void verifyStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("Store not found"));

        store.setIsVerified(true);
        storeRepository.save(store);
    }

    private AdminStoreDTO mapToDTO(Store store) {
        return AdminStoreDTO.builder()
                .id(store.getId())
                .name(store.getName())
                .status(store.getStatus())
                .subscriptionStatus(store.getSubscriptionStatus())
                .subscriptionEndsAt(store.getSubscriptionEndsAt())
                .isVerified(store.getIsVerified())
                .createdAt(store.getCreatedAt())
                // .userCount() - TODO: Need count queries
                .build();
    }
}
