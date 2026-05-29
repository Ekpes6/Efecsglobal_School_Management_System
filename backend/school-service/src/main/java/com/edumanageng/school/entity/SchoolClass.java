package com.edumanageng.school.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "school_classes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(nullable = false, length = 100)
    private String name; // e.g., "Primary 1", "JSS 2", "SSS 3"

    @Column(name = "class_arm", length = 10)
    private String classArm; // e.g., "A", "B", "C"

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private School.AcademicLevel level; // CRECHE, NURSERY, PRIMARY, SECONDARY

    @Column(name = "class_teacher_id")
    private Long classTeacherId;

    @Column(name = "capacity")
    private Integer capacity;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
