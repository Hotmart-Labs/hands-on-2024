package com.hotmart.handson.util;

import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class FilenameUtils {

    public Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
          .filter(f -> f.contains("."))
          .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public String buildRandomFilename(String originalFilename) {
        var fileNameBuilder = new StringBuilder(UUID.randomUUID().toString());
        getExtension(originalFilename).ifPresent(ext -> fileNameBuilder.append(".").append(ext));
        return fileNameBuilder.toString();
    }
}
