package com.edumanageng.file.repository;

import com.edumanageng.file.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findBySchoolId(Long schoolId);
    List<FileMetadata> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<FileMetadata> findByUploaderUserId(Long userId);
}
