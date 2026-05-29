package com.edumanageng.guardian.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity @Table(name = "guardians")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Guardian {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long userId; // Links to auth-service user

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    private String alternatePhone;
    private String occupation;
    private String address;
    private String state;
    private String lga;
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GuardianStatus status;

    @CreationTimestamp private LocalDateTime createdAt;
    @UpdateTimestamp private LocalDateTime updatedAt;

    public enum GuardianStatus { ACTIVE, INACTIVE, SUSPENDED }

    public String getFullName() { return firstName + " " + lastName; }
}
