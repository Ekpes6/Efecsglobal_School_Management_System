package com.edumanageng.academic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "results",
    uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "subject_id", "session_id", "term"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "subject_name", length = 100)
    private String subjectName;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "class_name", length = 100)
    private String className;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "session_name", length = 20)
    private String sessionName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Term term;

    // Nigerian grading: CA1 + CA2 + Exam = Total (out of 100)
    @Column(name = "ca1_score", precision = 5, scale = 2)
    private BigDecimal ca1Score;  // 10 marks

    @Column(name = "ca2_score", precision = 5, scale = 2)
    private BigDecimal ca2Score;  // 10 marks

    @Column(name = "ca3_score", precision = 5, scale = 2)
    private BigDecimal ca3Score;  // 10 marks (optional)

    @Column(name = "exam_score", precision = 5, scale = 2)
    private BigDecimal examScore; // 70 marks

    @Column(name = "total_score", precision = 5, scale = 2)
    private BigDecimal totalScore; // 100 marks

    @Column(name = "grade", length = 5)
    private String grade; // A1, B2, B3, C4, C5, C6, D7, E8, F9

    @Column(name = "grade_remark", length = 50)
    private String gradeRemark; // Excellent, Very Good, Good, etc.

    @Column(name = "position_in_class")
    private Integer positionInClass;

    @Column(name = "teacher_remark", length = 500)
    private String teacherRemark;

    @Column(name = "is_published", nullable = false)
    private boolean isPublished;

    @Column(name = "teacher_id")
    private Long teacherId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Term { FIRST_TERM, SECOND_TERM, THIRD_TERM }
}
