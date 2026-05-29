package com.edumanageng.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity @Table(name = "notification_logs")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificationLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long schoolId;
    private Long recipientUserId;
    private String recipientEmail;
    private String recipientPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Channel channel; // EMAIL or SMS

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String errorMessage;

    @CreationTimestamp private LocalDateTime createdAt;
    @UpdateTimestamp private LocalDateTime updatedAt;

    public enum NotificationType {
        RESULT_PUBLISHED, FEE_DUE, FEE_PAID,
        ENROLLMENT_CONFIRMED, ACCOUNT_CREATED, GENERAL
    }
    public enum Channel { EMAIL, SMS }
    public enum Status { PENDING, SENT, FAILED }
}
