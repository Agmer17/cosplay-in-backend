package com.agmerrizky.cosplayin.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ServerStorage {

    private final Path PUBLIC_STORAGE = Paths.get("").resolve("uploads").resolve("public").toAbsolutePath();
    private final Path PRIVATE_STORAGE = Paths.get("").resolve("uploads").resolve("private").toAbsolutePath();

    private final Set<String> ALLOWED_TYPES = Set.of(
            "image/",
            "video/",
            "audio/",
            "application/");

    private final Tika tika;

    public Path savePublicFile(MultipartFile file, String... dst) throws IOException {
        Path destination = resolveDestination(PUBLIC_STORAGE, dst);
        return processAndSave(file, destination);
    }

    public Path savePrivateFile(MultipartFile file, String... dst) throws IOException {
        Path destination = resolveDestination(PRIVATE_STORAGE, dst);
        return processAndSave(file, destination);
    }

    // --- PRIVATE HELPER METHODS ---
    private Path processAndSave(MultipartFile file, Path destination) throws IOException {
        String[] tp = validateFileType(file);
        String filename = UUID.randomUUID().toString();
        Path finalDest = destination.resolve(filename + "." + tp[1]);
        return saveFileToDisk(file, finalDest);
    }

    private String[] validateFileType(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            String mimeType = tika.detect(is);
            String[] typeData = getMediaType(mimeType);
            if (typeData == null || !ALLOWED_TYPES.contains(typeData[0])) {
                throw new IOException(
                        "UNSUPPORTED FILE TYPE! The current supported types are image, video, audio, and document");
            }

            return typeData;
        }
    }

    public boolean validateWantedType(MultipartFile file, String want) throws IOException {
        try (InputStream is = file.getInputStream()) {
            String mimeType = tika.detect(is);
            String[] typeData = getMediaType(mimeType);
            if (typeData == null || !typeData[0].equals(want)) {
                return false;
            }

            return true;
        }
    }

    private Path saveFileToDisk(MultipartFile file, Path destination) throws IOException {
        if (destination.getParent() != null) {
            Files.createDirectories(destination.getParent());
        }

        try (InputStream is = file.getInputStream()) {
            Files.copy(is, destination, StandardCopyOption.REPLACE_EXISTING);
        }

        return destination;
    }

    private Path resolveDestination(Path baseStorage, String... paths) {
        Path finalPath = baseStorage;
        for (String path : paths) {
            finalPath = finalPath.resolve(path);
        }
        return finalPath;
    }

    public String[] getMediaType(String mt) {
        if (mt == null)
            return null;

        String[] type = new String[2];
        int slashIndex = mt.indexOf("/");

        if (slashIndex != -1) {
            type[0] = mt.substring(0, slashIndex + 1);
            type[1] = mt.substring(slashIndex + 1);
            return type;
        }
        return null;
    }

    private void deleteFile(Path file) throws IOException {
        Files.deleteIfExists(file);
    }

    public void deletePublicFile(String... paths) throws IOException {
        Path file = resolveDestination(PUBLIC_STORAGE, paths);
        deleteFile(file);
    }

    public void deletePrivateFile(String... paths) throws IOException {
        Path file = resolveDestination(PRIVATE_STORAGE, paths);
        deleteFile(file);
    }
}