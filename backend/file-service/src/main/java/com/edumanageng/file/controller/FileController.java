package com.edumanageng.file.controller;

import com.edumanageng.file.entity.FileMetadata;
import com.edumanageng.file.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<FileMetadata> uploadFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam Long schoolId,
        @RequestParam Long uploaderUserId,
        @RequestParam(defaultValue = "GENERAL") String entityType,
        @RequestParam(required = false) Long entityId,
        @RequestParam(defaultValue = "false") boolean isPublic) throws IOException {

        FileMetadata metadata = fileStorageService.storeFile(file, schoolId, uploaderUserId, entityType, entityId, isPublic);
        return ResponseEntity.status(HttpStatus.CREATED).body(metadata);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileMetadata> getFileMetadata(@PathVariable Long id) {
        return ResponseEntity.ok(fileStorageService.getFileMetadata(id));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws IOException {
        FileMetadata metadata = fileStorageService.getFileMetadata(id);
        Path filePath = fileStorageService.loadFile(metadata.getFilePath());
        URI fileUri = Objects.requireNonNull(filePath.toUri());
        Resource resource = new UrlResource(fileUri);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(Objects.requireNonNull(metadata.getFileType())))
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + metadata.getOriginalName() + "\"")
            .body(resource);
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<FileMetadata>> getByEntity(@PathVariable String entityType,
                                                          @PathVariable Long entityId) {
        return ResponseEntity.ok(fileStorageService.getFilesByEntity(entityType, entityId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFile(@PathVariable Long id) throws IOException {
        fileStorageService.deleteFile(id);
        return ResponseEntity.ok(Map.of("message", "File deleted successfully"));
    }
}
