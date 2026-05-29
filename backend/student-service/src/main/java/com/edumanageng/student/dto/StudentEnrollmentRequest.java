package com.edumanageng.student.dto;

import com.edumanageng.student.entity.Student;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentEnrollmentRequest {

    @NotNull(message = "School ID is required")
    private Long schoolId;

    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    private String lastName;

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
    private Long classId;

    @NotNull(message = "Academic level is required")
    private Student.AcademicLevel academicLevel;

    private LocalDate admissionDate;
    private String nin;
}
