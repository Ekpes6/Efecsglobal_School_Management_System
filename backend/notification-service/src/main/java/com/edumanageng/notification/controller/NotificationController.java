package com.edumanageng.notification.controller;

import com.edumanageng.notification.entity.NotificationLog;
import com.edumanageng.notification.repository.NotificationLogRepository;
import com.edumanageng.notification.service.EmailService;
import com.edumanageng.notification.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;
    private final SmsService smsService;
    private final NotificationLogRepository logRepository;

    @PostMapping("/send/email")
    public ResponseEntity<Map<String, String>> sendEmail(@RequestBody Map<String, String> body) {
        emailService.sendEmail(
            body.get("schoolId") != null ? Long.parseLong(body.get("schoolId")) : null,
            body.get("userId") != null ? Long.parseLong(body.get("userId")) : null,
            body.get("email"),
            body.get("subject"),
            body.get("message"),
            NotificationLog.NotificationType.valueOf(body.getOrDefault("type", "GENERAL"))
        );
        return ResponseEntity.accepted().body(Map.of("message", "Email queued for delivery"));
    }

    @PostMapping("/send/sms")
    public ResponseEntity<Map<String, String>> sendSms(@RequestBody Map<String, String> body) {
        smsService.sendSms(
            body.get("schoolId") != null ? Long.parseLong(body.get("schoolId")) : null,
            body.get("userId") != null ? Long.parseLong(body.get("userId")) : null,
            body.get("phone"),
            body.get("message"),
            NotificationLog.NotificationType.valueOf(body.getOrDefault("type", "GENERAL"))
        );
        return ResponseEntity.accepted().body(Map.of("message", "SMS queued for delivery"));
    }

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<Page<NotificationLog>> getSchoolNotifications(
        @PathVariable Long schoolId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(logRepository.findBySchoolId(schoolId, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NotificationLog>> getUserNotifications(
        @PathVariable Long userId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(logRepository.findByRecipientUserId(userId, pageable));
    }
}
