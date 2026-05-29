package com.edumanageng.school.controller;

import com.edumanageng.school.dto.SchoolRegistrationRequest;
import com.edumanageng.school.dto.SchoolResponse;
import com.edumanageng.school.entity.AcademicSession;
import com.edumanageng.school.entity.School;
import com.edumanageng.school.entity.SchoolClass;
import com.edumanageng.school.service.SchoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/schools")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    @PostMapping("/register")
    public ResponseEntity<SchoolResponse> registerSchool(@Valid @RequestBody SchoolRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(schoolService.registerSchool(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolResponse> getSchool(@PathVariable Long id) {
        return ResponseEntity.ok(schoolService.getSchool(id));
    }

    @GetMapping
    public ResponseEntity<Page<SchoolResponse>> getAllSchools(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(schoolService.getAllSchools(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchoolResponse> updateSchool(@PathVariable Long id,
                                                       @Valid @RequestBody SchoolRegistrationRequest request) {
        return ResponseEntity.ok(schoolService.updateSchool(id, request));
    }

    // Academic Sessions
    @PostMapping("/{schoolId}/sessions")
    public ResponseEntity<AcademicSession> createSession(
        @PathVariable Long schoolId,
        @RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            schoolService.createAcademicSession(
                schoolId,
                body.get("name"),
                LocalDate.parse(body.get("startDate")),
                LocalDate.parse(body.get("endDate"))
            )
        );
    }

    @GetMapping("/{schoolId}/sessions")
    public ResponseEntity<List<AcademicSession>> getSessions(@PathVariable Long schoolId) {
        return ResponseEntity.ok(schoolService.getSchoolSessions(schoolId));
    }

    @GetMapping("/{schoolId}/sessions/current")
    public ResponseEntity<AcademicSession> getCurrentSession(@PathVariable Long schoolId) {
        return ResponseEntity.ok(schoolService.getCurrentSession(schoolId));
    }

    // Classes
    @PostMapping("/{schoolId}/classes")
    public ResponseEntity<SchoolClass> createClass(
        @PathVariable Long schoolId,
        @RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            schoolService.createClass(
                schoolId,
                body.get("name"),
                body.get("arm"),
                School.AcademicLevel.valueOf(body.get("level")),
                body.get("capacity") != null ? Integer.parseInt(body.get("capacity")) : null
            )
        );
    }

    @GetMapping("/{schoolId}/classes")
    public ResponseEntity<List<SchoolClass>> getClasses(@PathVariable Long schoolId) {
        return ResponseEntity.ok(schoolService.getSchoolClasses(schoolId));
    }
}
