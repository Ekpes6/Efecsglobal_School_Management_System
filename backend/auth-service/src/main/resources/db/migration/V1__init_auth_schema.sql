-- EduManage NG - Auth Service Database Schema
-- V1: Initial schema

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    INDEX idx_role_name (name)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone_number VARCHAR(20) UNIQUE,
    password VARCHAR(255) NOT NULL,
    school_id BIGINT,
    status ENUM('ACTIVE','INACTIVE','SUSPENDED','PENDING_VERIFICATION') NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    refresh_token VARCHAR(500),
    password_reset_token VARCHAR(200),
    password_reset_expiry DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_phone (phone_number),
    INDEX idx_user_school (school_id)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Seed default roles
INSERT IGNORE INTO roles (name, description) VALUES
    ('SUPER_ADMIN', 'Platform super administrator'),
    ('SCHOOL_ADMIN', 'School administrator'),
    ('PRINCIPAL', 'School principal'),
    ('TEACHER', 'Teacher or class teacher'),
    ('STUDENT', 'Student portal access'),
    ('GUARDIAN', 'Parent or guardian'),
    ('BURSAR', 'Financial officer'),
    ('LIBRARIAN', 'Librarian');

-- Seed default super admin (password: Admin@1234)
INSERT IGNORE INTO users (first_name, last_name, email, phone_number, password, status, email_verified) VALUES
    ('Super', 'Admin', 'superadmin@edumanageng.com', '+2348000000000',
     '$2a$10$rCa7AGNlh1jXvL/WC.TGOu3r8LjPfqxW1h69c01bHwu8J8sBuUU.6',
     'ACTIVE', TRUE);

-- Link super admin to SUPER_ADMIN role
INSERT IGNORE INTO user_roles (user_id, role_id)
    SELECT u.id, r.id FROM users u, roles r
    WHERE u.email = 'superadmin@edumanageng.com' AND r.name = 'SUPER_ADMIN';
