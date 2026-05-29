package com.edumanageng.student.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "admission_number", nullable = false, length = 50)
    private String admissionNumber;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "blood_group", length = 5)
    private String bloodGroup;

    @Column(name = "genotype", length = 5)
    private String genotype;

    @Column(name = "religion", length = 50)
    private String religion;

    @Column(name = "nationality", length = 50)
    private String nationality;

    @Column(name = "state_of_origin", length = 50)
    private String stateOfOrigin;

    @Column(name = "lga_of_origin", length = 100)
    private String lgaOfOrigin;

    @Column(name = "address", length = 300)
    private String address;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "academic_level", length = 20)
    @Enumerated(EnumType.STRING)
    private AcademicLevel academicLevel;

    @Column(name = "admission_date")
    private LocalDate admissionDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "nin", length = 20)
    private String nin;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Gender { MALE, FEMALE }
    public enum StudentStatus { ACTIVE, GRADUATED, WITHDRAWN, SUSPENDED, DECEASED }
    public enum AcademicLevel { CRECHE, NURSERY, PRIMARY, SECONDARY }

    public String getFullName() {
        return firstName + (middleName != null ? " " + middleName : "") + " " + lastName;
    }
}
