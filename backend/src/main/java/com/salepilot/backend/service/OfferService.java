package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.dto.OfferDTO;
import com.salepilot.backend.entity.Offer;
import com.salepilot.backend.entity.User;
import com.salepilot.backend.exception.ConflictException;
import com.salepilot.backend.exception.NotFoundException;
import com.salepilot.backend.repository.OfferRepository;
import com.salepilot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service for Marketplace Offers.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OfferService {

    private final OfferRepository offerRepository;
    private final UserRepository userRepository;

    /**
     * Create an offer
     */
    public Offer createOffer(OfferDTO request, Long userId) {
        String storeId = TenantContext.getCurrentTenant();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Offer offer = Offer.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .status(Offer.OfferStatus.OPEN)
                .build();

        return offerRepository.save(offer);
    }

    /**
     * Accept an offer
     */
    public Offer acceptOffer(Long offerId, Long acceptingUserId) {
        String storeId = TenantContext.getCurrentTenant();
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new NotFoundException("Offer not found"));

        if (!offer.getStoreId().equals(storeId)) {
            throw new SecurityException("Unauthorized access");
        }

        if (offer.getStatus() != Offer.OfferStatus.OPEN) {
            throw new ConflictException("Offer is not open");
        }

        User acceptingUser = userRepository.findById(acceptingUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Prevent self-acceptance
        if (offer.getUser().getId().equals(acceptingUserId)) {
            throw new ConflictException("Cannot accept your own offer");
        }

        offer.setAcceptedBy(acceptingUser);
        offer.setStatus(Offer.OfferStatus.ACCEPTED);

        return offerRepository.save(offer);
    }

    /**
     * Complete an offer
     */
    public Offer completeOffer(Long offerId, Long userId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new NotFoundException("Offer not found"));

        // Only creator or acceptor can complete (business logic may vary)
        if (!offer.getUser().getId().equals(userId) && !offer.getAcceptedBy().getId().equals(userId)) {
            throw new SecurityException("Not authorized to complete this offer");
        }

        offer.setStatus(Offer.OfferStatus.COMPLETED);
        return offerRepository.save(offer);
    }

    /**
     * Find nearby offers
     */
    @Transactional(readOnly = true)
    public Page<Offer> findNearby(BigDecimal lat, BigDecimal lon, double radiusKm, Pageable pageable) {
        String storeId = TenantContext.getCurrentTenant();
        // Simple bounding box for demo
        // 1 deg lat ~= 111 km
        BigDecimal delta = BigDecimal.valueOf(radiusKm / 111.0);

        BigDecimal minLat = lat.subtract(delta);
        BigDecimal maxLat = lat.add(delta);
        BigDecimal minLon = lon.subtract(delta); // Approximation (ignores cos(lat))
        BigDecimal maxLon = lon.add(delta);

        return offerRepository.findNearbyOffers(storeId, minLat, maxLat, minLon, maxLon, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Offer> getAllOffers(Pageable pageable) {
        String storeId = TenantContext.getCurrentTenant();
        return offerRepository.findByStoreIdAndStatusOrderByCreatedAtDesc(storeId, Offer.OfferStatus.OPEN, pageable);
    }
}
