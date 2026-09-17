package com.clouddoc.manager.document;

import com.clouddoc.manager.document.dto.DocumentResponse;
import com.clouddoc.manager.document.dto.DocumentUploadRequest;
import com.clouddoc.manager.security.CurrentUserProvider;
import com.clouddoc.manager.storage.CloudStorageService;
import com.clouddoc.manager.storage.CloudUploadResult;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {

    private final DocumentRepository repository;
    private final CloudStorageService storageService;
    private final CurrentUserProvider currentUserProvider;

    public DocumentService(DocumentRepository repository, CloudStorageService storageService,
                           CurrentUserProvider currentUserProvider) {
        this.repository = repository;
        this.storageService = storageService;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public DocumentResponse upload(DocumentUploadRequest request, MultipartFile file) {
        validateFile(file);
        String ownerId = currentUserProvider.currentUserId();
        CloudUploadResult upload = storageService.upload(file, ownerId);
        try {
            Document document = new Document(request.title().trim(), request.category().trim(),
                    request.description().trim(), file.getOriginalFilename(), file.getSize(),
                    file.getContentType(), upload.publicUrl(), upload.providerKey(), upload.provider(),
                    OffsetDateTime.now(), ownerId);
            return DocumentResponse.from(repository.save(document));
        } catch (RuntimeException exception) {
            try {
                storageService.delete(upload);
            } catch (RuntimeException cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public Page<DocumentResponse> search(String search, String category, Pageable pageable) {
        return repository.search(currentUserProvider.currentUserId(), normalize(search), normalize(category), pageable)
                .map(DocumentResponse::from);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("A non-empty document file is required.");
        }
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            throw new IllegalArgumentException("The document must have a file name.");
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
