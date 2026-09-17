package com.clouddoc.manager.document.dto;

import com.clouddoc.manager.document.Document;
import java.time.OffsetDateTime;

public record DocumentResponse(Long id, String title, String category, String description,
                               String fileName, long size, String type, String cloudUrl,
                               OffsetDateTime uploadTimestamp) {
    public static DocumentResponse from(Document document) {
        return new DocumentResponse(document.getId(), document.getTitle(), document.getCategory(),
                document.getDescription(), document.getFileName(), document.getSize(), document.getType(),
                document.getCloudUrl(), document.getUploadedAt());
    }
}
