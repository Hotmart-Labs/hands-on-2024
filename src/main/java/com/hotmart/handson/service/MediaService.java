package com.hotmart.handson.service;

import com.hotmart.handson.util.FilenameUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MediaService {

    @Value("${hands-on.media.path}")
    private String mediaPath;

    public Optional<String> save(Optional<MultipartFile> file) throws IOException {
        if (file.isPresent()) {
            String filename = FilenameUtils.buildRandomFilename(file.get().getOriginalFilename());
            File destinationFile = new File(mediaPath, filename);
            file.get().transferTo(destinationFile);
            return Optional.of(filename);
        } else {
            return Optional.empty();
        }
    }
}
