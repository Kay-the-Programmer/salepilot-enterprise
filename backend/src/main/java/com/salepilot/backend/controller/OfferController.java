package com.salepilot.backend.controller;

import com.salepilot.backend.dto.OfferDTO;
import com.salepilot.backend.entity.Offer;
import com.salepilot.backend.service.OfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * REST Controller for Marketplace Offers.
 */
@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
@Tag(name = "Marketplace", description = "Offer and marketplace endpoints")
public class OfferController {

    private final OfferService offerService;

    // Helper to get current user ID - in prod would use SecurityContext
    private Long getCurrentUserId() {
        // Placeholder: assume userID 1 for dev, or extract from JWT
        return 1L;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Create an offer")
    public ResponseEntity<OfferDTO> createOffer(@Valid @RequestBody OfferDTO request) {
        Offer offer = offerService.createOffer(request, getCurrentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(offer));
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Accept an offer")
    public ResponseEntity<OfferDTO> acceptOffer(@PathVariable Long id) {
        Offer offer = offerService.acceptOffer(id, getCurrentUserId());
        return ResponseEntity.ok(mapToDTO(offer));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Mark offer as completed")
    public ResponseEntity<OfferDTO> completeOffer(@PathVariable Long id) {
        Offer offer = offerService.completeOffer(id, getCurrentUserId());
        return ResponseEntity.ok(mapToDTO(offer));
    }

    @GetMapping
    @Operation(summary = "List open offers")
    public ResponseEntity<Page<OfferDTO>> listOffers(
            @RequestParam(required = false) BigDecimal lat,
            @RequestParam(required = false) BigDecimal lon,
            @RequestParam(defaultValue = "10") double radius,
            Pageable pageable) {

        Page<Offer> offers;
        if (lat != null && lon != null) {
            offers = offerService.findNearby(lat, lon, radius, pageable);
        } else {
            offers = offerService.getAllOffers(pageable);
        }

        return ResponseEntity.ok(offers.map(this::mapToDTO));
    }

    private OfferDTO mapToDTO(Offer offer) {
        return OfferDTO.builder()
                .id(offer.getId())
                .userId(offer.getUser().getIdAsString()) // Assuming method exists or use String.valueOf
                .userName(offer.getUser().getFirstName() + " " + offer.getUser().getLastName())
                .title(offer.getTitle())
                .description(offer.getDescription())
                .latitude(offer.getLatitude())
                .longitude(offer.getLongitude())
                .status(offer.getStatus())
                .createdAt(offer.getCreatedAt())
                .acceptedByUserId(offer.getAcceptedBy() != null ? offer.getAcceptedBy().getIdAsString() : null)
                .build();
    }
}
