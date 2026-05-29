package com.edumanageng.guardian.controller;

import com.edumanageng.guardian.entity.Guardian;
import com.edumanageng.guardian.entity.GuardianStudentLink;
import com.edumanageng.guardian.service.GuardianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/guardians")
@RequiredArgsConstructor
public class GuardianController {

    private final GuardianService guardianService;

    @PostMapping
    public ResponseEntity<Guardian> registerGuardian(@RequestBody Guardian guardian) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guardianService.registerGuardian(guardian));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Guardian> getGuardian(@PathVariable Long id) {
        return ResponseEntity.ok(guardianService.getGuardian(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Guardian> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(guardianService.getGuardianByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Guardian> updateGuardian(@PathVariable Long id, @RequestBody Guardian update) {
        return ResponseEntity.ok(guardianService.updateGuardian(id, update));
    }

    @PostMapping("/{guardianId}/wards")
    public ResponseEntity<GuardianStudentLink> linkWard(@PathVariable Long guardianId,
                                                        @RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            guardianService.linkStudentToGuardian(
                guardianId,
                Long.parseLong(body.get("studentId")),
                Long.parseLong(body.get("schoolId")),
                body.get("studentName"),
                body.get("admissionNumber"),
                body.get("relationship"),
                Boolean.parseBoolean(body.getOrDefault("isPrimary", "false"))
            )
        );
    }

    @GetMapping("/{guardianId}/wards")
    public ResponseEntity<List<GuardianStudentLink>> getWards(@PathVariable Long guardianId) {
        return ResponseEntity.ok(guardianService.getGuardianWards(guardianId));
    }

    @DeleteMapping("/{guardianId}/wards/{studentId}")
    public ResponseEntity<Map<String, String>> unlinkWard(@PathVariable Long guardianId,
                                                          @PathVariable Long studentId) {
        guardianService.unlinkStudent(guardianId, studentId);
        return ResponseEntity.ok(Map.of("message", "Ward unlinked"));
    }

    @GetMapping("/{guardianId}/wards/{studentId}/check")
    public ResponseEntity<Map<String, Boolean>> checkAccess(@PathVariable Long guardianId,
                                                            @PathVariable Long studentId) {
        return ResponseEntity.ok(Map.of("isGuardian", guardianService.isGuardianOfStudent(guardianId, studentId)));
    }
}
