package com.edumanageng.file.service;

import com.edumanageng.file.entity.FileMetadata;
import com.edumanageng.file.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final FileMetadataRepository fileMetadataRepository;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    private static final Set<String> ALLOWED_TYPES = Set.of(
        "image/jpeg", "image/png", "image/gif",
        "application/pdf", "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024; // 10MB

    @Transactional
    public FileMetadata storeFile(MultipartFile file, Long schoolId, Long uploaderUserId,
                                  String entityType, Long entityId, boolean isPublic) throws IOException {
        validateFile(file);

        String originalName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")) : "";
        String storedName = UUID.randomUUID() + ext;

        Path uploadPath = Paths.get(uploadDir, String.valueOf(schoolId));
        Files.createDirectories(uploadPath);
        Path targetPath = uploadPath.resolve(storedName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        FileMetadata metadata = FileMetadata.builder()
            .schoolId(schoolId)
            .uploaderUserId(uploaderUserId)
            .fileName(storedName)
            .originalName(originalName)
            .fileType(file.getContentType())
            .fileSize(file.getSize())
            .filePath(schoolId + "/" + storedName)
            .entityType(entityType)
            .entityId(entityId)
            .isPublic(isPublic)
            .build();

        return fileMetadataRepository.save(Objects.requireNonNull(metadata));
    }

    public FileMetadata getFileMetadata(Long id) {
        return fileMetadataRepository.findById(Objects.requireNonNull(id))
            .orElseThrow(() -> new IllegalArgumentException("File not found: " + id));
    }

    public Path loadFile(String filePath) {
        return Paths.get(uploadDir).resolve(filePath);
    }

    @Transactional
    public void deleteFile(Long id) throws IOException {
        FileMetadata metadata = getFileMetadata(id);
        Path filePath = Paths.get(uploadDir).resolve(metadata.getFilePath());
        Files.deleteIfExists(filePath);
        fileMetadataRepository.delete(metadata);
    }

    public List<FileMetadata> getFilesByEntity(String entityType, Long entityId) {
        return fileMetadataRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("File type not allowed: " + contentType);
        }
    }
}
