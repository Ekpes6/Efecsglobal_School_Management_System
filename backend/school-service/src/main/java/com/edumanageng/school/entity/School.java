package com.edumanageng.school.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "schools")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "registration_number", unique = true, length = 50)
    private String registrationNumber;

    @Column(name = "rc_number", length = 50)
    private String rcNumber;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 50)
    private String state;

    @Column(name = "lga", length = 100)
    private String lga;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(length = 150)
    private String email;

    @Column(length = 200)
    private String website;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "admin_user_id", nullable = false)
    private Long adminUserId;

    @Column(name = "subscription_plan", length = 50)
    @Enumerated(EnumType.STRING)
    private SubscriptionPlan subscriptionPlan;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SchoolStatus status;

    @Column(name = "max_students")
    private Integer maxStudents;

    @ElementCollection
    @CollectionTable(name = "school_levels", joinColumns = @JoinColumn(name = "school_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private List<AcademicLevel> levels;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum SchoolStatus { ACTIVE, INACTIVE, SUSPENDED, PENDING_APPROVAL }
    public enum SubscriptionPlan { FREE, BASIC, STANDARD, PREMIUM, ENTERPRISE }
    public enum AcademicLevel { CRECHE, NURSERY, PRIMARY, SECONDARY, ALL }
}
