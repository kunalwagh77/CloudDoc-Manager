package com.clouddoc.manager.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

class LocalDiskStorageServiceTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void uploadWritesFileAndDeleteRemovesIt() throws Exception {
        LocalDiskStorageService service = new LocalDiskStorageService(
                temporaryDirectory.toString(), "http://localhost:8080/uploads");
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf",
                "document".getBytes());

        CloudUploadResult result = service.upload(file, "user-1");
        Path storedFile = temporaryDirectory.resolve(result.providerKey());

        assertEquals("local", result.provider());
        assertTrue(result.publicUrl().endsWith("/" + result.providerKey()));
        assertTrue(Files.exists(storedFile));
        assertEquals("document", Files.readString(storedFile));

        service.delete(result);

        assertTrue(Files.notExists(storedFile));
    }
}