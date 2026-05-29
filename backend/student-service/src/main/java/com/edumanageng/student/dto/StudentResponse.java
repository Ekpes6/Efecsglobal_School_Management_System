package com.edumanageng.student.dto;

import com.edumanageng.student.entity.Student;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentResponse {
    private Long id;
    private Long schoolId;
    private String admissionNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private LocalDate dateOfBirth;
    private Student.Gender gender;
    private String bloodGroup;
    private String genotype;
    private String religion;
    private String nationality;
    private String stateOfOrigin;
    private String lgaOfOrigin;
    private String address;
    private String phoneNumber;
    private String email;
    private String photoUrl;
    private Long classId;
    private Student.AcademicLevel academicLevel;
    private LocalDate admissionDate;
    private Student.StudentStatus status;
    private LocalDateTime createdAt;
}
