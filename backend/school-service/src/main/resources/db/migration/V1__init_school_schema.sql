-- EduManage NG - School Service Database Schema
-- V1: Initial schema

CREATE TABLE IF NOT EXISTS schools (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    registration_number VARCHAR(50) UNIQUE,
    rc_number VARCHAR(50),
    address VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(50) NOT NULL,
    lga VARCHAR(100),
    phone_number VARCHAR(20),
    email VARCHAR(150),
    website VARCHAR(200),
    logo_url VARCHAR(500),
    admin_user_id BIGINT NOT NULL,
    subscription_plan ENUM('FREE','BASIC','STANDARD','PREMIUM','ENTERPRISE') DEFAULT 'FREE',
    status ENUM('ACTIVE','INACTIVE','SUSPENDED','PENDING_APPROVAL') NOT NULL DEFAULT 'ACTIVE',
    max_students INT DEFAULT 50,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_school_state (state),
    INDEX idx_school_status (status),
    INDEX idx_school_admin (admin_user_id)
);

CREATE TABLE IF NOT EXISTS school_levels (
    school_id BIGINT NOT NULL,
    level ENUM('CRECHE','NURSERY','PRIMARY','SECONDARY','ALL') NOT NULL,
    PRIMARY KEY (school_id, level),
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS academic_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    school_id BIGINT NOT NULL,
    name VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    current_term ENUM('FIRST_TERM','SECOND_TERM','THIRD_TERM'),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session_school (school_id),
    INDEX idx_session_current (school_id, is_current),
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS school_classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    school_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    class_arm VARCHAR(10),
    level ENUM('CRECHE','NURSERY','PRIMARY','SECONDARY','ALL') NOT NULL,
    class_teacher_id BIGINT,
    capacity INT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_class_school (school_id),
    INDEX idx_class_level (school_id, level),
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);
