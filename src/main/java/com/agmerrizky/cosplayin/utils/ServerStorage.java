package com.agmerrizky.cosplayin.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.agmerrizky.cosplayin.common.exceptions.BadRequestsException;
import com.agmerrizky.cosplayin.common.type.MediaType;

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

    public List<Path> savePublicFiles(MultipartFile[] files, String... dst) throws IOException {
        Path destination = resolveDestination(PUBLIC_STORAGE, dst);
        return processAndSaveMultiple(files, destination);
    }

    public List<Path> savePrivateFiles(MultipartFile[] files, String... dst) throws IOException {
        Path destination = resolveDestination(PRIVATE_STORAGE, dst);
        return processAndSaveMultiple(files, destination);
    }

    private List<Path> processAndSaveMultiple(MultipartFile[] files, Path destination) throws IOException {
        if (files == null || files.length == 0) {
            return List.of();
        }

        Path[] finalDests = new Path[files.length];
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            String[] tp = validateFileType(file);
            String filename = UUID.randomUUID().toString();
            finalDests[i] = destination.resolve(filename + "." + tp[1]);
        }

        return saveFileToDisk(files, finalDests);
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

    private List<Path> saveFileToDisk(MultipartFile[] files, Path[] dests) throws IOException {
        if (files.length != dests.length) {
            throw new BadRequestsException("files and dests the destinantion must be the same length");
        }

        var semaphore = new Semaphore(10);
        var saved = new CopyOnWriteArrayList<Path>();
        var failed = new AtomicBoolean(false);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int i = 0; i < files.length; i++) {
                final var file = files[i];
                final var dest = dests[i];

                var future = CompletableFuture.runAsync(() -> {
                    // langsung skip kalau ada yang udah gagal
                    if (failed.get())
                        return;

                    try {
                        semaphore.acquire();
                        try {
                            if (!failed.get()) {
                                Path result = saveFileToDisk(file, dest);
                                saved.add(result);
                            }
                        } finally {
                            semaphore.release();
                        }
                    } catch (IOException | InterruptedException e) {
                        failed.set(true);
                        Thread.currentThread().interrupt();
                        throw new CompletionException(e);
                    }
                }, executor);

                futures.add(future);
            }

            try {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            } catch (CompletionException e) {
                // cancel semua future yang masih pending
                futures.forEach(f -> f.cancel(true));

                // cleanup file yang keburu ke-save
                for (Path path : saved) {
                    Files.deleteIfExists(path);
                }

                Throwable cause = e.getCause();
                if (cause instanceof IOException io)
                    throw io;
                throw new IOException("failed to save the files" + cause.getMessage(), cause);
            }
        }

        return List.copyOf(saved);
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

    public static MediaType getMediaTypeFromFilename(String filename) {
        String extension = getExtension(filename);

        return switch (extension) {
            case "jpg", "jpeg", "png", "webp" -> MediaType.IMAGE;
            case "gif" -> MediaType.GIF;
            case "mp4" -> MediaType.VIDEO;
            case "mp3", "wav", "ogg", "m4a", "aac" -> MediaType.VOICE_MESSAGE;
            default -> throw new IllegalArgumentException(
                    "Unsupported media extension: " + extension);
        };
    }

    private static String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');

        if (lastDot == -1 || lastDot == filename.length() - 1) {
            throw new IllegalArgumentException(
                    "Filename does not have a valid extension: " + filename);
        }

        return filename.substring(lastDot + 1).toLowerCase();
    }
}