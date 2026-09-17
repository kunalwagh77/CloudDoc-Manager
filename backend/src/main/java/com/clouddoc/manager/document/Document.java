package com.clouddoc.manager.document;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_size", nullable = false)
    private long size;

    @Column(name = "content_type", nullable = false, length = 160)
    private String type;

    @Column(name = "cloud_url", nullable = false, length = 2048)
    private String cloudUrl;

    @Column(name = "provider_key", nullable = false, length = 512)
    private String providerKey;

    @Column(name = "storage_provider", nullable = false, length = 32)
    private String storageProvider;

    @Column(name = "uploaded_at", nullable = false)
    private OffsetDateTime uploadedAt;

    @Column(name = "owner_id", nullable = false, length = 160)
    private String ownerId;

    protected Document() {
    }

    public Document(String title, String category, String description, String fileName, long size,
                    String type, String cloudUrl, String providerKey, String storageProvider,
                    OffsetDateTime uploadedAt, String ownerId) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.fileName = fileName;
        this.size = size;
        this.type = type;
        this.cloudUrl = cloudUrl;
        this.providerKey = providerKey;
        this.storageProvider = storageProvider;
        this.uploadedAt = uploadedAt;
        this.ownerId = ownerId;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getFileName() { return fileName; }
    public long getSize() { return size; }
    public String getType() { return type; }
    public String getCloudUrl() { return cloudUrl; }
    public String getProviderKey() { return providerKey; }
    public String getStorageProvider() { return storageProvider; }
    public OffsetDateTime getUploadedAt() { return uploadedAt; }
    public String getOwnerId() { return ownerId; }
}
