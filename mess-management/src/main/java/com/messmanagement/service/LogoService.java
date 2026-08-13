package com.messmanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Stores the mess's uploaded logo image on disk (outside the packaged
 * app, so it survives restarts and rebuilds) and remembers which
 * filename/extension is currently active via a small marker file.
 *
 * Only one logo is kept at a time - uploading a new one replaces the old.
 */
@Service
public class LogoService {

    private static final String BASE_FILENAME = "logo";
    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024; // 5 MB

    private final Path uploadDir;
    private final Path markerFile;

    public LogoService(@Value("${app.upload.dir:uploads}") String uploadDirProperty) {
        this.uploadDir = Paths.get(uploadDirProperty).toAbsolutePath().normalize();
        this.markerFile = this.uploadDir.resolve(".logo-filename");
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create upload directory: " + this.uploadDir, e);
        }
    }

    /**
     * Saves the uploaded image, replacing any previous logo.
     * Throws IllegalArgumentException with a friendly message for
     * anything that isn't a small, valid image file.
     */
    public void uploadLogo(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please choose an image file to upload.");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("Image is too large - please use a file under 5 MB.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Please upload an image file (PNG, JPG, GIF, SVG, etc.).");
        }

        String originalName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalName.substring(dotIndex); // includes the dot, e.g. ".png"
        }

        deleteExistingLogoFiles();

        String newFilename = BASE_FILENAME + extension;
        Path target = uploadDir.resolve(newFilename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        Files.writeString(markerFile, newFilename, StandardCharsets.UTF_8);
    }

    /**
     * Returns the current logo's filename (as served under /uploads/**),
     * or null if no logo has been uploaded yet.
     */
    public String getCurrentLogoFilename() {
        try {
            if (!Files.exists(markerFile)) {
                return null;
            }
            String filename = Files.readString(markerFile, StandardCharsets.UTF_8).trim();
            if (filename.isEmpty() || !Files.exists(uploadDir.resolve(filename))) {
                return null;
            }
            return filename;
        } catch (IOException e) {
            return null;
        }
    }

    public boolean hasLogo() {
        return getCurrentLogoFilename() != null;
    }

    public Path getUploadDir() {
        return uploadDir;
    }

    private void deleteExistingLogoFiles() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadDir, BASE_FILENAME + ".*")) {
            for (Path existing : stream) {
                Files.deleteIfExists(existing);
            }
        }
    }
}
