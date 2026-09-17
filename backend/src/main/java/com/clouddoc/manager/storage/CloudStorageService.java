package com.clouddoc.manager.storage;

import org.springframework.web.multipart.MultipartFile;

public interface CloudStorageService {
    CloudUploadResult upload(MultipartFile file, String ownerId);
    void delete(CloudUploadResult upload);
}
