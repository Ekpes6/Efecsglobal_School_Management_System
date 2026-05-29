package com.edumanageng.guardian.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity @Table(name = "guardian_student_links",
    uniqueConstraints = @UniqueConstraint(columnNames = {"guardian_id", "student_id"}))
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GuardianStudentLink {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long guardianId;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long schoolId;

    private String studentName;
    private String admissionNumber;

    @Column(nullable = false)
    private String relationship; // e.g. Father, Mother, Uncle, Guardian

    private boolean isPrimary;

    @Enumerated(EnumType.STRING)
    private LinkStatus status;

    @CreationTimestamp private LocalDateTime createdAt;

    public enum LinkStatus { ACTIVE, INACTIVE }
}
