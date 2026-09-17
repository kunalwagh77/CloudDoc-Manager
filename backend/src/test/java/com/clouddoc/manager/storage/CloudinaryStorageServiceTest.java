package com.clouddoc.manager.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

class CloudinaryStorageServiceTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void missingCredentialsUseLocalStorage() throws Exception {
        LocalDiskStorageService localStorage = new LocalDiskStorageService(
                temporaryDirectory.toString(), "http://localhost:8080/uploads");
        CloudinaryStorageService service = new CloudinaryStorageService(
                mock(Cloudinary.class), "clouddoc", localStorage, false);

        CloudUploadResult result = service.upload(file(), "user-1");

        assertEquals("local", result.provider());
        assertEquals(1, Files.list(temporaryDirectory.resolve("user-1")).count());
    }

    @Test
    void cloudinaryExceptionFallsBackToLocalStorage() throws Exception {
        LocalDiskStorageService localStorage = new LocalDiskStorageService(
                temporaryDirectory.toString(), "http://localhost:8080/uploads");
        Cloudinary cloudinary = mock(Cloudinary.class);
        Uploader uploader = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any(Map.class))).thenThrow(new RuntimeException("Cloudinary unavailable"));
        CloudinaryStorageService service = new CloudinaryStorageService(
                cloudinary, "clouddoc", localStorage, true);

        CloudUploadResult result = service.upload(file(), "user-1");

        assertEquals("local", result.provider());
        assertEquals(1, Files.list(temporaryDirectory.resolve("user-1")).count());
    }

    private MockMultipartFile file() {
        return new MockMultipartFile("file", "report.pdf", "application/pdf", "document".getBytes());
    }
}