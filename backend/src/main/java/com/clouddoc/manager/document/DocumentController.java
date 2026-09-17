package com.clouddoc.manager.document;

import com.clouddoc.manager.document.dto.DocumentResponse;
import com.clouddoc.manager.document.dto.DocumentUploadRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Documents", description = "Upload and search cloud documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload a document with metadata")
    public DocumentResponse upload(
            @Valid @RequestPart("metadata") DocumentUploadRequest metadata,
            @RequestPart("file") MultipartFile file) {
        return documentService.upload(metadata, file);
    }

    @GetMapping
    @Operation(summary = "List, search, and filter documents")
    public Page<DocumentResponse> search(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size) {
        int boundedSize = Math.min(Math.max(size, 1), 100);
        return documentService.search(search, category,
                PageRequest.of(Math.max(page, 0), boundedSize, Sort.by(Sort.Direction.DESC, "uploadedAt")));
    }
}
