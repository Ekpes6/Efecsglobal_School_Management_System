package com.edumanageng.notification.service;

import com.edumanageng.notification.entity.NotificationLog;
import com.edumanageng.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final NotificationLogRepository logRepository;

    @Value("${spring.mail.username:noreply@edumanageng.com}")
    private String fromEmail;

    @Async
    public void sendEmail(Long schoolId, Long recipientUserId, String recipientEmail,
                          String subject, String message, NotificationLog.NotificationType type) {
        NotificationLog logEntry = NotificationLog.builder()
            .schoolId(schoolId)
            .recipientUserId(recipientUserId)
            .recipientEmail(recipientEmail)
            .type(type)
            .channel(NotificationLog.Channel.EMAIL)
            .subject(subject)
            .message(message)
            .status(NotificationLog.Status.PENDING)
            .build();

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(fromEmail);
            mail.setTo(recipientEmail);
            mail.setSubject("[EduManage NG] " + subject);
            mail.setText(message);
            mailSender.send(mail);
            logEntry.setStatus(NotificationLog.Status.SENT);
            log.info("Email sent to {}: {}", recipientEmail, subject);
        } catch (Exception e) {
            logEntry.setStatus(NotificationLog.Status.FAILED);
            logEntry.setErrorMessage(e.getMessage());
            log.error("Failed to send email to {}: {}", recipientEmail, e.getMessage());
        } finally {
            logRepository.save(Objects.requireNonNull(logEntry));
        }
    }
}
