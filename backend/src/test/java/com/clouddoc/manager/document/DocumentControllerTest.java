package com.clouddoc.manager.document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.clouddoc.manager.document.dto.DocumentResponse;
import com.clouddoc.manager.document.dto.DocumentUploadRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {

    @Mock
    private DocumentService documentService;

    private DocumentController controller;

    @BeforeEach
    void setUp() {
        controller = new DocumentController(documentService);
    }

    @Test
    void uploadDelegatesMetadataAndFile() {
        DocumentUploadRequest metadata = new DocumentUploadRequest("Report", "Legal", "Details");
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf", new byte[] {1});
        DocumentResponse response = new DocumentResponse(1L, "Report", "Legal", "Details", "report.pdf", 1,
                "application/pdf", "https://cdn/report.pdf", null);
        when(documentService.upload(metadata, file)).thenReturn(response);

        assertEquals(response, controller.upload(metadata, file));
        verify(documentService).upload(metadata, file);
    }

    @Test
    void searchClampsPageAndSizeBeforeDelegating() {
        Page<DocumentResponse> page = new PageImpl<>(List.of());
        when(documentService.search(any(), any(), any(Pageable.class))).thenReturn(page);

        assertEquals(page, controller.search(" report ", "Legal", -3, 500));

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(documentService).search(org.mockito.ArgumentMatchers.eq(" report "),
                org.mockito.ArgumentMatchers.eq("Legal"), pageable.capture());
        assertEquals(0, pageable.getValue().getPageNumber());
        assertEquals(100, pageable.getValue().getPageSize());
        assertEquals("uploadedAt: DESC", pageable.getValue().getSort().toString());
    }

    @Test
    void searchUsesMinimumPageSizeAndNonNegativePage() {
        when(documentService.search(any(), any(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        controller.search("", "", 2, 0);

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(documentService).search(org.mockito.ArgumentMatchers.eq(""), org.mockito.ArgumentMatchers.eq(""),
                pageable.capture());
        assertEquals(2, pageable.getValue().getPageNumber());
        assertEquals(1, pageable.getValue().getPageSize());
    }
}
