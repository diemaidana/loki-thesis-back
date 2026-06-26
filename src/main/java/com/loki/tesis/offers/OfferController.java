package com.loki.tesis.offers;

import com.loki.tesis.offers.dto.CreateOfferRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @PostMapping
    public ResponseEntity<OfferCreatedResponseDTO> create(@Valid @RequestBody CreateOfferRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(offerService.create(request));
    }

}
