package com.clouddoc.manager.storage;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "s3")
public class S3StorageService implements CloudStorageService {

    private final S3Client client;
    private final String bucket;
    private final String region;
    private final String publicBaseUrl;

    public S3StorageService(@Value("${app.storage.s3.bucket:}") String bucket,
                            @Value("${app.storage.s3.region:us-east-1}") String region,
                            @Value("${app.storage.s3.public-base-url:}") String publicBaseUrl) {
        this.bucket = bucket;
        this.region = region;
        this.publicBaseUrl = publicBaseUrl;
        this.client = S3Client.builder().region(Region.of(region)).build();
    }

    @Override
    public CloudUploadResult upload(MultipartFile file, String ownerId) {
        String key = ownerId + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        try {
            client.putObject(PutObjectRequest.builder().bucket(bucket).key(key)
                    .contentType(file.getContentType()).build(), RequestBody.fromBytes(file.getBytes()));
            String baseUrl = publicBaseUrl.isBlank()
                    ? "https://" + bucket + ".s3." + region + ".amazonaws.com"
                    : publicBaseUrl.replaceAll("/$", "");
            return new CloudUploadResult(baseUrl + "/" + key, key, "s3");
        } catch (IOException exception) {
            throw new StorageException("S3 upload failed", exception);
        }
    }

    @Override
    public void delete(CloudUploadResult upload) {
        client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(upload.providerKey()).build());
    }
}
