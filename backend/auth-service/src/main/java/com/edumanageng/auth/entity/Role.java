package com.edumanageng.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @Column(length = 200)
    private String description;

    public enum RoleName {
        SUPER_ADMIN,        // Platform super admin
        SCHOOL_ADMIN,       // School administrator
        PRINCIPAL,          // School principal
        TEACHER,            // Teacher/Class teacher
        STUDENT,            // Student (for student portal)
        GUARDIAN,           // Parent/Guardian
        BURSAR,             // Financial officer
        LIBRARIAN           // Librarian
    }
}
