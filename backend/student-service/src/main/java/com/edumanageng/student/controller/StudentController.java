package com.edumanageng.student.controller;

import com.edumanageng.student.dto.*;
import com.edumanageng.student.entity.Student;
import com.edumanageng.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentResponse> enrollStudent(@Valid @RequestBody StudentEnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.enrollStudent(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudent(id));
    }

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<Page<StudentResponse>> getSchoolStudents(
        @PathVariable Long schoolId,
        @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(studentService.getSchoolStudents(schoolId, pageable));
    }

    @GetMapping("/school/{schoolId}/search")
    public ResponseEntity<Page<StudentResponse>> searchStudents(
        @PathVariable Long schoolId,
        @RequestParam String query,
        @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(studentService.searchStudents(schoolId, query, pageable));
    }

    @GetMapping("/school/{schoolId}/admission/{admissionNumber}")
    public ResponseEntity<StudentResponse> getByAdmissionNumber(
        @PathVariable Long schoolId,
        @PathVariable String admissionNumber) {
        return ResponseEntity.ok(studentService.getStudentByAdmissionNumber(admissionNumber, schoolId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id,
                                                         @Valid @RequestBody StudentEnrollmentRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> updateStatus(@PathVariable Long id,
                                                             @RequestBody Map<String, String> body) {
        studentService.updateStudentStatus(id, Student.StudentStatus.valueOf(body.get("status")));
        return ResponseEntity.ok(Map.of("message", "Student status updated"));
    }

    @GetMapping("/school/{schoolId}/count")
    public ResponseEntity<Map<String, Long>> countStudents(@PathVariable Long schoolId) {
        return ResponseEntity.ok(Map.of("activeStudents", studentService.countActiveStudents(schoolId)));
    }
}
