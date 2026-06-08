package com.edumanageng.academic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance",
    uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "class_id", "attendance_date", "term", "session_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "student_name", length = 150)
    private String studentName;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "class_name", length = 100)
    private String className;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Result.Term term;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    @Column(name = "marked_by")
    private Long markedBy;

    @Column(length = 300)
    private String remark;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum AttendanceStatus { PRESENT, ABSENT, LATE, EXCUSED }
}
