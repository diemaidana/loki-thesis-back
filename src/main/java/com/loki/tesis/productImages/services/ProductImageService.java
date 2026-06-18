package com.loki.tesis.productImages.services;

import com.loki.tesis.productImages.ProductImage;
import com.loki.tesis.productImages.ProductImageRepository;
import com.loki.tesis.products.Product;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductImageService {
    private final ProductImageRepository imageRepository;
    private final FileStorageService fileStorageService;

    public String storeImage(MultipartFile multipartFile) {
        return fileStorageService.store(multipartFile);
    }

    @Transactional
    public void create(String filename, Product product, Integer displayOrder) {
        ProductImage image = new ProductImage();
        image.setUrl(filename);
        image.setDisplayOrder(displayOrder);
        image.setProduct(product);

        imageRepository.save(image);
    }

    public String getCoverImageUrl(Long productId) {
        return imageRepository.findImageUrl(productId, 0)
                .orElseThrow(() -> new EntityNotFoundException("Image not found."));
    }

    public List<String> getImages(Long id) {
            return imageRepository.findImagesUrl(id);
    }
}
