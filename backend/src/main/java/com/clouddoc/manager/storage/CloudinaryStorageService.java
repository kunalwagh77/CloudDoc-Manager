package com.clouddoc.manager.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Primary
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "cloudinary", matchIfMissing = true)
public class CloudinaryStorageService implements CloudStorageService {

    private final Cloudinary cloudinary;
    private final LocalDiskStorageService localStorage;
    private final String folder;
    private final boolean credentialsConfigured;

    @Autowired
    public CloudinaryStorageService(
            @Value("${app.storage.cloudinary.cloud-name:}") String cloudName,
            @Value("${app.storage.cloudinary.api-key:}") String apiKey,
            @Value("${app.storage.cloudinary.api-secret:}") String apiSecret,
            @Value("${app.storage.cloudinary.folder:clouddoc}") String folder,
            LocalDiskStorageService localStorage) {
            this(new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret)), folder, localStorage,
                !cloudName.isBlank() && !apiKey.isBlank() && !apiSecret.isBlank());
            }

            CloudinaryStorageService(Cloudinary cloudinary, String folder,
                         LocalDiskStorageService localStorage, boolean credentialsConfigured) {
            this.cloudinary = cloudinary;
        this.localStorage = localStorage;
        this.folder = folder;
            this.credentialsConfigured = credentialsConfigured;
    }

    @Override
    public CloudUploadResult upload(MultipartFile file, String ownerId) {
        if (!credentialsConfigured) {
            return localStorage.upload(file, ownerId);
        }
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folder + "/" + ownerId,
                    "resource_type", "auto"));
            return new CloudUploadResult(
                    String.valueOf(result.get("secure_url")),
                    String.valueOf(result.get("public_id")),
                    "cloudinary");
        } catch (Exception cloudinaryException) {
            try {
                return localStorage.upload(file, ownerId);
            } catch (RuntimeException fallbackException) {
                fallbackException.addSuppressed(cloudinaryException);
                throw fallbackException;
            }
        }
    }

    @Override
    public void delete(CloudUploadResult upload) {
        if ("local".equals(upload.provider())) {
            localStorage.delete(upload);
            return;
        }
        try {
            cloudinary.uploader().destroy(upload.providerKey(), ObjectUtils.asMap("resource_type", "auto"));
        } catch (IOException exception) {
            throw new StorageException("Cloudinary cleanup failed", exception);
        }
    }
}
