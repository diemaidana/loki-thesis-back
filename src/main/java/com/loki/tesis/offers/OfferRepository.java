package com.loki.tesis.offers;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfferRepository extends JpaRepository<OfferEntity, Long>, JpaSpecificationExecutor<OfferEntity> {
    boolean existsByProductIdAndBuyerId(Long id, Long buyerId);

    Optional<OfferEntity> findByOfferCodeAndStatusIn(UUID offerId, List<OfferStatus> statuses);

    @Modifying
    @Query("UPDATE OfferEntity o SET o.status = :expiredStatus " +
           "WHERE o.status IN :activeStatuses AND o.expiresAt <= :now")
    int expireOffersOlderThan(
            @Param("expiredStatus") OfferStatus expiredStatus,
            @Param("activeStatuses") List<OfferStatus> activeStatuses
    );
}
