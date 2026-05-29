package com.edumanageng.file.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity @Table(name = "file_metadata")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FileMetadata {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long schoolId;
    private Long uploaderUserId;

    @Column(nullable = false)
    private String fileName; // stored name (UUID-based)

    @Column(nullable = false)
    private String originalName;

    private String fileType; // MIME type
    private Long fileSize; // in bytes
    private String filePath; // relative path in uploads dir
    private String entityType; // e.g. STUDENT, SCHOOL, ASSIGNMENT
    private Long entityId;
    private boolean isPublic;

    @CreationTimestamp
    private LocalDateTime uploadedAt;
}
