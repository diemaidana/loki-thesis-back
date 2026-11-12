package com.loki.tesis.offers;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.offers.dto.CounterOfferRequestDTO;
import com.loki.tesis.offers.dto.CreateOfferRequestDto;
import com.loki.tesis.offers.dto.OfferResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @PostMapping
    public ResponseEntity<OfferResponseDTO> create(@Valid @RequestBody CreateOfferRequestDto request,
                                                   @AuthenticationPrincipal Credential credential) {
        return ResponseEntity.status(HttpStatus.CREATED).body(offerService.create(request, credential));
    }

    @PostMapping("/{offerId}")
    public ResponseEntity<OfferResponseDTO> acceptOffer(@PathVariable UUID offerId,
                                                        @AuthenticationPrincipal Credential credential) {
        return ResponseEntity.ok(offerService.acceptOffer(offerId, credential));
    }

    @PostMapping("/counter-offer")
    public ResponseEntity<OfferResponseDTO> counterOffer(@Valid @RequestBody CounterOfferRequestDTO counterOffer,
                                                         @AuthenticationPrincipal Credential credential) {
        return ResponseEntity.ok(offerService.counterOffer(counterOffer, credential));
    }

    @PostMapping("/reject-offer/{offerId}")
    public ResponseEntity<OfferResponseDTO> rejectOffer(@PathVariable UUID offerId,
                                                        @AuthenticationPrincipal Credential credential) {
        return ResponseEntity.ok(offerService.rejectOffer(offerId, credential));
    }

    @DeleteMapping("/{offerId}")
    public ResponseEntity<Void> cancelOffer(@PathVariable UUID offerId,
                                            @AuthenticationPrincipal Credential credential) {
        offerService.cancelOffer(offerId, credential);
        return ResponseEntity.noContent().build();
    }
}
