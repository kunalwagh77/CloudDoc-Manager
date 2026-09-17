package com.clouddoc.manager.document;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.clouddoc.manager.document.dto.DocumentResponse;
import com.clouddoc.manager.security.CurrentUserProvider;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class DocumentMappingTest {

    @Test
    void documentResponseMapsDocumentFields() {
        OffsetDateTime uploadedAt = OffsetDateTime.now();
        Document document = new Document("Report", "Legal", "Details", "report.pdf", 42,
                "application/pdf", "https://cdn/report.pdf", "documents/report", "s3", uploadedAt, "user-1");

        DocumentResponse response = DocumentResponse.from(document);

        assertEquals(document.getId(), response.id());
        assertEquals(document.getTitle(), response.title());
        assertEquals(document.getCategory(), response.category());
        assertEquals(document.getDescription(), response.description());
        assertEquals(document.getFileName(), response.fileName());
        assertEquals(document.getSize(), response.size());
        assertEquals(document.getType(), response.type());
        assertEquals(document.getCloudUrl(), response.cloudUrl());
        assertEquals(uploadedAt, response.uploadTimestamp());
    }

    @Test
    void currentUserProviderReturnsConfiguredDevelopmentPrincipal() {
        assertEquals("configured-user", new CurrentUserProvider("configured-user").currentUserId());
    }
}
