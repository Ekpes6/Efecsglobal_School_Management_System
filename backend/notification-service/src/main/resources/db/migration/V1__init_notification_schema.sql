-- EduManage NG - Notification Service Database Schema
-- V1: Initial schema

CREATE TABLE IF NOT EXISTS notification_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    school_id BIGINT,
    recipient_user_id BIGINT,
    recipient_email VARCHAR(150),
    recipient_phone VARCHAR(20),
    type ENUM('RESULT_PUBLISHED','FEE_DUE','FEE_PAID','ENROLLMENT_CONFIRMED','ACCOUNT_CREATED','GENERAL') NOT NULL,
    channel ENUM('EMAIL','SMS') NOT NULL,
    subject VARCHAR(300) NOT NULL,
    message TEXT,
    status ENUM('PENDING','SENT','FAILED') DEFAULT 'PENDING',
    error_message VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_notif_school (school_id),
    INDEX idx_notif_user (recipient_user_id),
    INDEX idx_notif_status (status),
    INDEX idx_notif_type (type)
);
