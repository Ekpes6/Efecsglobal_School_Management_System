package com.edumanageng.notification.service;

import com.edumanageng.notification.entity.NotificationLog;
import com.edumanageng.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    private final NotificationLogRepository logRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${termii.api-key:}")
    private String apiKey;

    @Value("${termii.base-url:https://api.ng.termii.com/api}")
    private String baseUrl;

    @Value("${termii.sender-id:EduManageNG}")
    private String senderId;

    @Async
    public void sendSms(Long schoolId, Long recipientUserId, String recipientPhone,
                        String message, NotificationLog.NotificationType type) {
        NotificationLog logEntry = NotificationLog.builder()
            .schoolId(schoolId)
            .recipientUserId(recipientUserId)
            .recipientPhone(recipientPhone)
            .type(type)
            .channel(NotificationLog.Channel.SMS)
            .subject("SMS Notification")
            .message(message)
            .status(NotificationLog.Status.PENDING)
            .build();

        try {
            if (apiKey == null || apiKey.isBlank()) {
                log.warn("Termii API key not configured. SMS not sent to {}", recipientPhone);
                logEntry.setStatus(NotificationLog.Status.FAILED);
                logEntry.setErrorMessage("API key not configured");
            } else {
                Map<String, String> payload = Map.of(
                    "to", recipientPhone,
                    "from", senderId,
                    "sms", message,
                    "type", "plain",
                    "api_key", apiKey,
                    "channel", "generic"
                );
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
                restTemplate.postForEntity(baseUrl + "/sms/send", request, String.class);
                logEntry.setStatus(NotificationLog.Status.SENT);
                log.info("SMS sent to {}", recipientPhone);
            }
        } catch (Exception e) {
            logEntry.setStatus(NotificationLog.Status.FAILED);
            logEntry.setErrorMessage(e.getMessage());
            log.error("Failed to send SMS to {}: {}", recipientPhone, e.getMessage());
        } finally {
            logRepository.save(Objects.requireNonNull(logEntry));
        }
    }
}
