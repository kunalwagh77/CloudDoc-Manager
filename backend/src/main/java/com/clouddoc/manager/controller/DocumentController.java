package com.clouddoc.manager.controller;

import com.clouddoc.manager.document.Document;
import com.clouddoc.manager.document.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    private final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;

    @GetMapping
    public ResponseEntity<Page<Document>> getAllDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(documentRepository.findAll(PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<?> uploadDocument(
            @RequestParam("title") String title,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam("file") MultipartFile file) {
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String generatedFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + generatedFileName);
            Files.write(filePath, file.getBytes());

            String cloudUrl = "/uploads/" + generatedFileName;

            Document doc = new Document(
                title,                                 // 1. title
                category != null ? category : "General",// 2. category
                file.getOriginalFilename(),            // 3. fileName
                file.getContentType(),                 // 4. type / mimeType
                file.getSize(),                        // 5. size (long)
                cloudUrl,                              // 6. cloudUrl
                "local-user",                          // 7. owner / userId
                "active",                              // 8. status
                "public",                              // 9. accessLevel
                OffsetDateTime.now(),                  // 10. uploadTimestamp (OffsetDateTime)
                "system"                               // 11. description / source
            );

            Document savedDoc = documentRepository.save(doc);
            return ResponseEntity.ok(savedDoc);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error uploading file: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        documentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
