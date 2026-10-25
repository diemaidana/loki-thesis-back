package com.loki.tesis.shared.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum ImageTypeSupported {

    JPA("image/jpeg", "jpg"),
    JPEG("image/jpeg", "jpeg"),
    PNG("image/png", "png"),
    WEBP("image/webp", "webp");

    private final String mimeType;
    private final String extension;

    public static boolean isSupported(String type) {
        List<ImageTypeSupported> types = List.of(values());
        for (ImageTypeSupported i : types){
            System.out.println("mimeType: " + i.mimeType);
            System.out.println("extension: " + i.extension);
        }
        return Arrays.stream(values()).anyMatch(t -> t.getMimeType().equalsIgnoreCase(type));
    }

}
