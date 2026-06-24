package com.loki.tesis.shared.validation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

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
        return Arrays.stream(values()).anyMatch(t -> t.getMimeType().equalsIgnoreCase(type));
    }

}
