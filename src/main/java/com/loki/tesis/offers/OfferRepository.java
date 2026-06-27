package com.loki.tesis.offers;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<OfferEntity, Long>, JpaSpecificationExecutor<OfferEntity> {
    boolean findByProductIdAndBuyerId(Long id, Long buyerId);
}
