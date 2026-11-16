package com.loki.tesis.offers;

import com.loki.tesis.auth.credential.entity.Credential;
import com.loki.tesis.auth.exception.EmailNotVerifiedException;
import com.loki.tesis.auth.exception.ForbiddenException;
import com.loki.tesis.offers.dto.CounterOfferRequestDTO;
import com.loki.tesis.offers.dto.CreateOfferRequestDto;
import com.loki.tesis.offers.dto.OfferResponseDTO;
import com.loki.tesis.products.ProductStatus;
import com.loki.tesis.products.entities.Product;
import com.loki.tesis.products.ProductRepository;
import com.loki.tesis.user.entity.User;
import com.loki.tesis.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfferService {
    private final OfferRepository offerRepository;
    private final OfferMapper offerMapper;

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OfferResponseDTO create(CreateOfferRequestDto request, Credential credential) {
        Product product = productRepository.findByProductCodeAndStatus(request.productCode(), ProductStatus.PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException("Product not found."));

        User buyer = credential.getUser();

        User seller = product.getSeller();

        if(buyer.getUuid().equals(seller.getUuid()))
            throw new ForbiddenException("Buyer is not allowed.");

        validateOfferRequest(request, product, buyer);

        OfferEntity offer = offerMapper.toEntity(request);
        offer.setBuyer(buyer);
        offer.setSeller(seller);
        offer.setProduct(product);
        offer.setLastUserType(OfferUserType.BUYER);

        return offerMapper.toResponseDTO(offerRepository.save(offer));
    }

    private void validateOfferRequest(CreateOfferRequestDto request, Product product, User buyer) {
        if(offerRepository.existsByProductIdAndBuyerId(product.getId(), buyer.getId()))
            throw new IllegalArgumentException("There is an offer already made by this user for this product.");

        if(!validateAmountOffer(request.amount(), product))
            throw new IllegalArgumentException("Stock insufficient.");

        if(!validatePriceOffer(request.price(), product))
            throw new IllegalArgumentException("Price offered not valid.");
    }

    private boolean validatePriceOffer(BigDecimal price, Product product) {
        if(price.compareTo(product.getPriceMin()) >= 0 && price.compareTo(product.getPrice()) < 0)
            return true;
        return false;
    }

    private boolean validateAmountOffer(Integer amount, Product product) {
        return product.getStock() >= amount;
    }

    @Transactional
    public OfferResponseDTO acceptOffer(UUID offerId, Credential credential) {

        OfferEntity offer = getValidActiveOffer(offerId, credential);

        offer.setStatus(OfferStatus.ACCEPTED);

        // CREAR ORDER

        return offerMapper.toResponseDTO(offer);
    }

    @Transactional
    public OfferResponseDTO counterOffer(CounterOfferRequestDTO counterOffer, Credential credential) {
        OfferEntity offer = getValidActiveOffer(counterOffer.offerId(), credential);

        if(!validatePriceOffer(counterOffer.price(), offer.getProduct()))
            throw new IllegalArgumentException("Price offered not valid.");

        offer.setPrice(counterOffer.price());
        offer.setAmount(counterOffer.amount());
        offer.setStatus(OfferStatus.IN_PROGRESS);
        offer.changeLastUserType();

        return offerMapper.toResponseDTO(offer);
    }

    private OfferEntity getValidActiveOffer(UUID offerId, Credential credential) {
        if(!credential.isEmailVerified())
            throw new EmailNotVerifiedException("User email must be verified.");

        OfferEntity offer = findOfferOpenOrInProgress(offerId);

        if (!offer.isUserValid(credential.getUser())) {
            throw new ForbiddenException("User does not have permission to modify this offer.");
        }

        if(LocalDateTime.now().isAfter(offer.getExpiresAt()))
            throw new IllegalStateException("Offer has expired.");

        return offer;
    }

    private OfferEntity findOfferOpenOrInProgress(UUID offerId) {
        return offerRepository.findByOfferCodeAndStatusIn(offerId, List.of(OfferStatus.OPEN, OfferStatus.IN_PROGRESS))
                .orElseThrow(() -> new EntityNotFoundException("Offer not found."));
    }

    @Transactional
    public OfferResponseDTO rejectOffer(UUID offerId, Credential credential) {
        OfferEntity offer = getValidActiveOffer(offerId, credential);

        offer.setStatus(OfferStatus.REJECTED);

        return offerMapper.toResponseDTO(offer);
    }

    @Transactional
    public void cancelOffer(UUID offerId, Credential credential) {
        OfferEntity offer = getValidActiveOrUnpaidOffer(offerId, credential);

        offer.setStatus(OfferStatus.CANCELLED);

        // DELETE ORDER IF CREATED
    }

    private OfferEntity getValidActiveOrUnpaidOffer(UUID offerId, Credential credential) {
        if(!credential.isEmailVerified())
            throw new EmailNotVerifiedException("User email must be verified.");

        OfferEntity offer = offerRepository.findByOfferCodeAndStatusIn(offerId,
                        List.of(OfferStatus.OPEN, OfferStatus.IN_PROGRESS, OfferStatus.ACCEPTED))
                .orElseThrow(() -> new EntityNotFoundException("Offer not found."));

        User user = credential.getUser();

        if (!offer.getBuyer().getUuid().equals(user.getUuid())) {
            throw new ForbiddenException("User does not have permission to cancel this offer.");
        }

        return offer;
    }
}
