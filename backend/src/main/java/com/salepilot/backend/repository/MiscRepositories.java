package com.salepilot.backend.repository;

import com.salepilot.backend.entity.Payment;
import com.salepilot.backend.entity.Return;
import com.salepilot.backend.entity.StockTake;
import com.salepilot.backend.entity.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Additional repositories for remaining entities
 */

@Repository
interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findBySale_Id(Long saleId);

    Page<Payment> findByStoreId(String storeId, Pageable pageable);
}

@Repository
interface ReturnRepository extends JpaRepository<Return, Long> {
    Page<Return> findByStoreId(String storeId, Pageable pageable);
}

@Repository
interface StockTakeRepository extends JpaRepository<StockTake, Long> {
    Page<StockTake> findByStoreIdOrderByStartTimeDesc(String storeId, Pageable pageable);

    List<StockTake> findByStoreIdAndStatus(String storeId, StockTake.StockTakeStatus status);
}

@Repository
interface OfferRepository extends JpaRepository<Offer, Long> {
    Page<Offer> findByStoreIdOrderByCreatedAtDesc(String storeId, Pageable pageable);

    Page<Offer> findByStoreIdAndStatus(String storeId, Offer.OfferStatus status, Pageable pageable);
}
