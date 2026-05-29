-- EduManage NG - Guardian Service Database Schema
-- V1: Initial schema

CREATE TABLE IF NOT EXISTS guardians (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    phone_number VARCHAR(20) NOT NULL,
    alternate_phone VARCHAR(20),
    occupation VARCHAR(150),
    address VARCHAR(300),
    state VARCHAR(50),
    lga VARCHAR(100),
    profile_image_url VARCHAR(500),
    status ENUM('ACTIVE','INACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_guardian_phone (phone_number),
    INDEX idx_guardian_user (user_id)
);

CREATE TABLE IF NOT EXISTS guardian_student_links (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guardian_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    school_id BIGINT NOT NULL,
    student_name VARCHAR(200),
    admission_number VARCHAR(50),
    relationship VARCHAR(50) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    status ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_guardian_student (guardian_id, student_id),
    INDEX idx_link_guardian (guardian_id),
    INDEX idx_link_student (student_id),
    INDEX idx_link_school (school_id)
);
