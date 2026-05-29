package com.edumanageng.academic.controller;

import com.edumanageng.academic.dto.*;
import com.edumanageng.academic.entity.Result;
import com.edumanageng.academic.entity.Subject;
import com.edumanageng.academic.service.AcademicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AcademicController {

    private final AcademicService academicService;

    // Subjects
    @PostMapping("/api/v1/subjects")
    public ResponseEntity<Subject> createSubject(@RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            academicService.createSubject(
                Long.parseLong(body.get("schoolId")),
                body.get("name"),
                body.get("code"),
                body.get("description"),
                Subject.AcademicLevel.valueOf(body.get("level")),
                body.get("classId") != null ? Long.parseLong(body.get("classId")) : null,
                Boolean.parseBoolean(body.getOrDefault("isCompulsory", "true"))
            )
        );
    }

    @GetMapping("/api/v1/subjects/school/{schoolId}")
    public ResponseEntity<List<Subject>> getSubjectsBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(academicService.getSubjectsBySchool(schoolId));
    }

    @GetMapping("/api/v1/subjects/school/{schoolId}/class/{classId}")
    public ResponseEntity<List<Subject>> getSubjectsByClass(@PathVariable Long schoolId, @PathVariable Long classId) {
        return ResponseEntity.ok(academicService.getSubjectsByClass(schoolId, classId));
    }

    // Results
    @PostMapping("/api/v1/results")
    public ResponseEntity<ResultResponse> recordResult(@Valid @RequestBody RecordResultRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(academicService.recordResult(request));
    }

    @PutMapping("/api/v1/results/{id}")
    public ResponseEntity<ResultResponse> updateResult(@PathVariable Long id,
                                                       @Valid @RequestBody RecordResultRequest request) {
        return ResponseEntity.ok(academicService.updateResult(id, request));
    }

    @GetMapping("/api/v1/results/student/{studentId}")
    public ResponseEntity<List<ResultResponse>> getStudentResults(@PathVariable Long studentId) {
        return ResponseEntity.ok(academicService.getStudentResults(studentId));
    }

    @GetMapping("/api/v1/results/student/{studentId}/session/{sessionId}")
    public ResponseEntity<List<ResultResponse>> getStudentSessionResults(
        @PathVariable Long studentId, @PathVariable Long sessionId) {
        return ResponseEntity.ok(academicService.getStudentResultsBySession(studentId, sessionId));
    }

    @GetMapping("/api/v1/results/student/{studentId}/session/{sessionId}/term/{term}")
    public ResponseEntity<List<ResultResponse>> getTermResults(
        @PathVariable Long studentId,
        @PathVariable Long sessionId,
        @PathVariable Result.Term term) {
        return ResponseEntity.ok(academicService.getStudentTermResults(studentId, sessionId, term));
    }

    @GetMapping("/api/v1/results/student/{studentId}/published")
    public ResponseEntity<List<ResultResponse>> getPublishedResults(@PathVariable Long studentId) {
        return ResponseEntity.ok(academicService.getPublishedStudentResults(studentId));
    }

    @PostMapping("/api/v1/results/publish")
    public ResponseEntity<Map<String, String>> publishResults(@RequestBody Map<String, String> body) {
        academicService.publishResults(
            Long.parseLong(body.get("schoolId")),
            Long.parseLong(body.get("sessionId")),
            Result.Term.valueOf(body.get("term"))
        );
        return ResponseEntity.ok(Map.of("message", "Results published successfully"));
    }

    // Academic report
    @GetMapping("/api/v1/academic/report-card/{studentId}/session/{sessionId}/term/{term}")
    public ResponseEntity<List<ResultResponse>> getReportCard(
        @PathVariable Long studentId,
        @PathVariable Long sessionId,
        @PathVariable Result.Term term) {
        return ResponseEntity.ok(academicService.getStudentTermResults(studentId, sessionId, term));
    }
}
