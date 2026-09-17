package com.clouddoc.manager.document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.clouddoc.manager.document.dto.DocumentResponse;
import com.clouddoc.manager.document.dto.DocumentUploadRequest;
import com.clouddoc.manager.security.CurrentUserProvider;
import com.clouddoc.manager.storage.CloudStorageService;
import com.clouddoc.manager.storage.CloudUploadResult;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository repository;

    @Mock
    private CloudStorageService storageService;

    @Mock
    private CurrentUserProvider currentUserProvider;

    private DocumentService service;

    @BeforeEach
    void setUp() {
        service = new DocumentService(repository, storageService, currentUserProvider);
    }

    @Test
    void uploadTrimsMetadataAndPersistsUploadedDocument() {
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf",
                "content".getBytes());
        DocumentUploadRequest request = new DocumentUploadRequest("  Report  ", "  Legal ", "  Details  ");
        CloudUploadResult upload = new CloudUploadResult("https://cdn/report.pdf", "documents/report", "s3");
        when(currentUserProvider.currentUserId()).thenReturn("user-1");
        when(storageService.upload(file, "user-1")).thenReturn(upload);
        when(repository.save(any(Document.class))).thenAnswer(invocation -> {
            Document document = invocation.getArgument(0);
            return document;
        });

        DocumentResponse response = service.upload(request, file);

        assertEquals("Report", response.title());
        assertEquals("Legal", response.category());
        assertEquals("Details", response.description());
        assertEquals("report.pdf", response.fileName());
        assertEquals(upload.publicUrl(), response.cloudUrl());
        verify(repository).save(any(Document.class));
    }

    @Test
    void uploadDeletesCloudObjectWhenPersistenceFails() {
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf",
                "content".getBytes());
        DocumentUploadRequest request = new DocumentUploadRequest("Report", "Legal", "Details");
        CloudUploadResult upload = new CloudUploadResult("https://cdn/report.pdf", "documents/report", "s3");
        RuntimeException failure = new RuntimeException("database unavailable");
        when(currentUserProvider.currentUserId()).thenReturn("user-1");
        when(storageService.upload(file, "user-1")).thenReturn(upload);
        when(repository.save(any(Document.class))).thenThrow(failure);

        assertEquals(failure, assertThrows(RuntimeException.class, () -> service.upload(request, file)));
        verify(storageService).delete(upload);
    }

    @Test
    void uploadRejectsEmptyFileBeforeCallingDependencies() {
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf", new byte[0]);

        assertThrows(IllegalArgumentException.class,
                () -> service.upload(new DocumentUploadRequest("Report", "Legal", "Details"), file));
        verify(storageService, never()).upload(any(), any());
    }

    @Test
    void uploadRejectsFileWithoutName() {
        MockMultipartFile file = new MockMultipartFile("file", null, "application/pdf", "content".getBytes());

        assertThrows(IllegalArgumentException.class,
                () -> service.upload(new DocumentUploadRequest("Report", "Legal", "Details"), file));
        verify(storageService, never()).upload(any(), any());
    }

    @Test
    void searchNormalizesFiltersAndMapsResults() {
        Document document = new Document("Report", "Legal", "Details", "report.pdf", 7,
                "application/pdf", "https://cdn/report.pdf", "documents/report", "s3",
                OffsetDateTime.now(), "user-1");
        PageRequest pageable = PageRequest.of(0, 10);
        when(currentUserProvider.currentUserId()).thenReturn("user-1");
        when(repository.search("user-1", "report", "Legal", pageable))
                .thenReturn(new PageImpl<>(java.util.List.of(document), pageable, 1));

        Page<DocumentResponse> result = service.search(" report ", " Legal ", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Report", result.getContent().get(0).title());
        verify(repository).search(eq("user-1"), eq("report"), eq("Legal"), eq(pageable));
    }
}
