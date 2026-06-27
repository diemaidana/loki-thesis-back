package com.loki.tesis.offers;

import com.loki.tesis.offers.dto.CreateOfferRequestDto;
import com.loki.tesis.products.Product;
import com.loki.tesis.products.ProductRepository;
import com.loki.tesis.user.entity.User;
import com.loki.tesis.user.repository.UserRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;
    private final OfferMapper offerMapper;

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OfferCreatedResponseDTO create(CreateOfferRequestDto request) {
        Product product = productRepository.findByProductCode(request.productCode())
                .orElseThrow(() -> new EntityNotFoundException("Product not found."));

        User buyer = userRepository.findByUuid(request.buyerCode())
                .orElseThrow(() -> new EntityNotFoundException("Buyer user not found."));

        User seller = product.getSeller();

        validateOfferRequest(request, product, buyer);

        OfferEntity offer = offerMapper.toEntity(request);
        offer.setBuyer(buyer);
        offer.setSeller(seller);
        offer.setLastUserType(OfferUserType.BUYER);

        return null;
    }

    private void validateOfferRequest(CreateOfferRequestDto request, Product product, User buyer) {
        if(offerRepository.findByProductIdAndBuyerId(product.getId(), buyer.getId()))
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
}
