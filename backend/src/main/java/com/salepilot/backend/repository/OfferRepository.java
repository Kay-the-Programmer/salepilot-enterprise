package com.salepilot.backend.repository;

import com.salepilot.backend.entity.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * Repository for Offer entity.
 */
@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    /**
     * Find active offers by store (if applicable, though offers might be
     * cross-tenant in some designs)
     * Here assuming tenant isolation.
     */
    Page<Offer> findByStoreIdAndStatusOrderByCreatedAtDesc(String storeId, Offer.OfferStatus status, Pageable pageable);

    /**
     * Find nearby offers (Haversine formula approximation could be done in DB or
     * via PostGIS)
     * Using strict tenant isolation for now.
     */
    @Query("SELECT o FROM Offer o WHERE o.storeId = :storeId AND o.status = 'OPEN' AND " +
            "o.latitude BETWEEN :minLat AND :maxLat AND " +
            "o.longitude BETWEEN :minLon AND :maxLon")
    Page<Offer> findNearbyOffers(@Param("storeId") String storeId,
            @Param("minLat") BigDecimal minLat, @Param("maxLat") BigDecimal maxLat,
            @Param("minLon") BigDecimal minLon, @Param("maxLon") BigDecimal maxLon,
            Pageable pageable);

    /**
     * Note: For real geospatial queries, we would use Hibernate Spatial / PostGIS.
     * This is a bounding box approximation.
     */
}
