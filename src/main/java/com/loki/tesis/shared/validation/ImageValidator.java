package com.loki.tesis.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class ImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024L;

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if(file == null || file.isEmpty()) return false;

        try {
            Tika tika = new Tika();
            String detectedType = tika.detect(file.getInputStream());
            return ImageTypeSupported.isSupported(detectedType);
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isSizeExceeded(MultipartFile file) {
        return file.getSize() < MAX_IMAGE_SIZE;
    }
}
