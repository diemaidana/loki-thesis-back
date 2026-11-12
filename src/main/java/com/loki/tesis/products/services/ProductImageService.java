package com.loki.tesis.products.services;

import com.cloudinary.Cloudinary;
import com.loki.tesis.products.entities.Product;
import com.loki.tesis.products.entities.ProductImage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductImageService {
    private final Cloudinary cloudinary;

    @Transactional
    public void addImages(Product product, List<MultipartFile> images) {
        Integer productImagesLength = product.getImagesSize();

        if(productImagesLength + images.size() > 5){
            throw new IllegalArgumentException("Product cannot exceed 5 images.");
        }

        String folderPath = "products/" + product.getProductCode().toString();

        Map<String, String> uploadOptions = Map.of(
                "folder", folderPath
        );

        for(MultipartFile image : images) {
            try {
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(), uploadOptions);

                ProductImage productImage = new ProductImage();
                productImage.setDisplayOrder(productImagesLength+1);
                productImage.setPublicId(uploadResult.get("public_id").toString());
                productImage.setSecureURL(uploadResult.get("secure_url").toString());

                product.addImage(productImage);
                productImagesLength++;
            } catch (IOException e) {
                throw new RuntimeException("Cloudinary connection error", e);
            }
        }
    }

    @Transactional
    public void deleteImage(Product product, String publicId) {
        ProductImage imageToDelete = product.getImages().stream()
                .filter(i -> i.getPublicId().equals(publicId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Image not found."));

        try {
            cloudinary.uploader().destroy(publicId, Map.of());
            product.deleteImage(imageToDelete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}