package com.loki.tesis.offers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OfferExpirationScheduler {
    private final OfferRepository offerRepository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void expireOldOffersTask() {
        LocalDateTime limitDate = LocalDateTime.now().minusHours(48);

        int updatedCount = offerRepository.expireOffersOlderThan(
                OfferStatus.EXPIRED,
                List.of(OfferStatus.OPEN, OfferStatus.IN_PROGRESS)
        );

        if(updatedCount > 0)
            log.info("Mantenimiento: {} ofertas expiraron por superar el limite.", updatedCount);
    }
}
