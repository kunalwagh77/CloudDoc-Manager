package com.clouddoc.manager.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalDiskStorageService implements CloudStorageService {

    private final Path rootDirectory;
    private final String publicBaseUrl;

    public LocalDiskStorageService(
            @Value("${app.storage.local.directory:uploads}") String directory,
            @Value("${app.storage.local.public-base-url:http://localhost:${server.port:8080}/uploads}") String publicBaseUrl) {
        this.rootDirectory = Path.of(directory).toAbsolutePath().normalize();
        this.publicBaseUrl = publicBaseUrl.replaceAll("/$", "");
    }

    @Override
    public CloudUploadResult upload(MultipartFile file, String ownerId) {
        String safeOwnerId = sanitize(ownerId);
        String safeFilename = sanitize(Path.of(file.getOriginalFilename()).getFileName().toString());
        String key = safeOwnerId + "/" + UUID.randomUUID() + "-" + safeFilename;
        Path destination = resolve(key);
        try {
            Files.createDirectories(destination.getParent());
            Files.write(destination, file.getBytes());
            return new CloudUploadResult(publicBaseUrl + "/" + key, key, "local");
        } catch (IOException exception) {
            throw new StorageException("Local disk upload failed", exception);
        }
    }

    @Override
    public void delete(CloudUploadResult upload) {
        try {
            Files.deleteIfExists(resolve(upload.providerKey()));
        } catch (IOException exception) {
            throw new StorageException("Local disk cleanup failed", exception);
        }
    }

    public Path rootDirectory() {
        return rootDirectory;
    }

    private Path resolve(String key) {
        Path resolved = rootDirectory.resolve(key).normalize();
        if (!resolved.startsWith(rootDirectory)) {
            throw new IllegalArgumentException("Invalid local storage key.");
        }
        return resolved;
    }

    private String sanitize(String value) {
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}