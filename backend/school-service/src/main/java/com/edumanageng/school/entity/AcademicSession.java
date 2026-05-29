package com.edumanageng.school.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "academic_sessions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AcademicSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(nullable = false, length = 20)
    private String name; // e.g. "2024/2025"

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "is_current", nullable = false)
    private boolean isCurrent;

    @Column(name = "current_term")
    @Enumerated(EnumType.STRING)
    private Term currentTerm;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum Term {
        FIRST_TERM, SECOND_TERM, THIRD_TERM
    }
}
