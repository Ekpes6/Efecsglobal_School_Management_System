package com.edumanageng.student.service;

import com.edumanageng.student.dto.*;
import com.edumanageng.student.entity.Student;
import com.edumanageng.student.exception.StudentException;
import com.edumanageng.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentResponse enrollStudent(StudentEnrollmentRequest request) {
        String admissionNumber = generateAdmissionNumber(request.getSchoolId(), request.getAcademicLevel());

        if (studentRepository.existsByAdmissionNumberAndSchoolId(admissionNumber, request.getSchoolId())) {
            admissionNumber = admissionNumber + "-" + System.currentTimeMillis() % 1000;
        }

        Student student = Student.builder()
            .schoolId(request.getSchoolId())
            .admissionNumber(admissionNumber)
            .firstName(request.getFirstName())
            .middleName(request.getMiddleName())
            .lastName(request.getLastName())
            .dateOfBirth(request.getDateOfBirth())
            .gender(request.getGender())
            .bloodGroup(request.getBloodGroup())
            .genotype(request.getGenotype())
            .religion(request.getReligion())
            .nationality(request.getNationality() != null ? request.getNationality() : "Nigerian")
            .stateOfOrigin(request.getStateOfOrigin())
            .lgaOfOrigin(request.getLgaOfOrigin())
            .address(request.getAddress())
            .phoneNumber(request.getPhoneNumber())
            .email(request.getEmail())
            .classId(request.getClassId())
            .academicLevel(request.getAcademicLevel())
            .admissionDate(request.getAdmissionDate() != null ? request.getAdmissionDate() : LocalDate.now())
            .status(Student.StudentStatus.ACTIVE)
            .nin(request.getNin())
            .build();

        student = studentRepository.save(Objects.requireNonNull(student));
        log.info("Student enrolled: {} {} (Admission No: {})", student.getFirstName(), student.getLastName(), student.getAdmissionNumber());
        return mapToResponse(student);
    }

    public StudentResponse getStudent(Long id) {
        Student student = studentRepository.findById(Objects.requireNonNull(id))
            .orElseThrow(() -> new StudentException("Student not found with ID: " + id));
        return mapToResponse(student);
    }

    public StudentResponse getStudentByAdmissionNumber(String admissionNumber, Long schoolId) {
        Student student = studentRepository.findByAdmissionNumberAndSchoolId(admissionNumber, schoolId)
            .orElseThrow(() -> new StudentException("Student not found: " + admissionNumber));
        return mapToResponse(student);
    }

    public Page<StudentResponse> getSchoolStudents(Long schoolId, Pageable pageable) {
        return studentRepository.findBySchoolId(schoolId, pageable).map(this::mapToResponse);
    }

    public Page<StudentResponse> searchStudents(Long schoolId, String query, Pageable pageable) {
        return studentRepository.searchStudents(schoolId, query, pageable).map(this::mapToResponse);
    }

    public StudentResponse updateStudent(Long id, StudentEnrollmentRequest request) {
        Student student = studentRepository.findById(Objects.requireNonNull(id))
            .orElseThrow(() -> new StudentException("Student not found with ID: " + id));

        student.setFirstName(request.getFirstName());
        student.setMiddleName(request.getMiddleName());
        student.setLastName(request.getLastName());
        student.setAddress(request.getAddress());
        student.setPhoneNumber(request.getPhoneNumber());
        student.setEmail(request.getEmail());
        student.setClassId(request.getClassId());
        student.setBloodGroup(request.getBloodGroup());
        student.setGenotype(request.getGenotype());

        return mapToResponse(studentRepository.save(student));
    }

    public void updateStudentStatus(Long id, Student.StudentStatus status) {
        Student student = studentRepository.findById(Objects.requireNonNull(id))
            .orElseThrow(() -> new StudentException("Student not found with ID: " + id));
        student.setStatus(status);
        studentRepository.save(student);
    }

    public long countActiveStudents(Long schoolId) {
        return studentRepository.countActiveStudentsBySchool(schoolId);
    }

    private String generateAdmissionNumber(Long schoolId, Student.AcademicLevel level) {
        String year = String.valueOf(LocalDate.now().getYear()).substring(2);
        String levelCode = switch (level) {
            case CRECHE -> "CR";
            case NURSERY -> "NU";
            case PRIMARY -> "PR";
            case SECONDARY -> "SS";
        };
        long count = studentRepository.countActiveStudentsBySchool(schoolId) + 1;
        return String.format("SCH%d/%s/%s/%04d", schoolId, levelCode, year, count);
    }

    private StudentResponse mapToResponse(Student student) {
        return StudentResponse.builder()
            .id(student.getId())
            .schoolId(student.getSchoolId())
            .admissionNumber(student.getAdmissionNumber())
            .firstName(student.getFirstName())
            .middleName(student.getMiddleName())
            .lastName(student.getLastName())
            .fullName(student.getFullName())
            .dateOfBirth(student.getDateOfBirth())
            .gender(student.getGender())
            .bloodGroup(student.getBloodGroup())
            .genotype(student.getGenotype())
            .religion(student.getReligion())
            .nationality(student.getNationality())
            .stateOfOrigin(student.getStateOfOrigin())
            .lgaOfOrigin(student.getLgaOfOrigin())
            .address(student.getAddress())
            .phoneNumber(student.getPhoneNumber())
            .email(student.getEmail())
            .photoUrl(student.getPhotoUrl())
            .classId(student.getClassId())
            .academicLevel(student.getAcademicLevel())
            .admissionDate(student.getAdmissionDate())
            .status(student.getStatus())
            .createdAt(student.getCreatedAt())
            .build();
    }
}
