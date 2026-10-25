package com.loki.tesis.productImages.services;

import com.github.f4b6a3.uuid.UuidCreator;
import com.loki.tesis.productImages.exceptions.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    @Value("${app.uploads.dir}")
    private String uploadDir;

    /*
        1. Create filename with UUID for security and originalNameFile.
        2. Get target path with upload directory.
        3. Create directory
        4. Copy file
     */
    public String store(MultipartFile file) {
        String filename = UuidCreator.getTimeOrderedEpoch() + "_" + file.getOriginalFilename();

        try {
            Path target = Paths.get(uploadDir).resolve(filename);
            Files.createDirectories(target.getParent());
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file", e);
        }

        return filename;
    }
}
