package com.edumanageng.financial.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "fee_structures")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FeeStructure {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long schoolId;

    private Long sessionId;
    private String sessionName;

    @Enumerated(EnumType.STRING)
    private AcademicLevel level;

    private String className;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeType feeType;

    private String feeDescription;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount; // in NGN (Naira)

    @Enumerated(EnumType.STRING)
    private Term term;

    private boolean isActive;

    @CreationTimestamp private LocalDateTime createdAt;
    @UpdateTimestamp private LocalDateTime updatedAt;

    public enum AcademicLevel { CRECHE, NURSERY, PRIMARY, SECONDARY }
    public enum FeeType { TUITION, DEVELOPMENT_LEVY, SPORTS, LIBRARY, EXAM, BUS, UNIFORM, MISCELLANEOUS }
    public enum Term { FIRST_TERM, SECOND_TERM, THIRD_TERM, ANNUAL }
}
