package com.edumanageng.academic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "subjects")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "subject_code", length = 20)
    private String subjectCode;

    @Column(length = 300)
    private String description;

    @Column(name = "academic_level")
    @Enumerated(EnumType.STRING)
    private AcademicLevel academicLevel;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "is_compulsory", nullable = false)
    private boolean isCompulsory;

    @Column(name = "class_id")
    private Long classId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum AcademicLevel { CRECHE, NURSERY, PRIMARY, SECONDARY }
}
