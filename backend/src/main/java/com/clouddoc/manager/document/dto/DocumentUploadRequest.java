package com.clouddoc.manager.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DocumentUploadRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 80) String category,
        @NotBlank @Size(max = 1000) String description) {
}
