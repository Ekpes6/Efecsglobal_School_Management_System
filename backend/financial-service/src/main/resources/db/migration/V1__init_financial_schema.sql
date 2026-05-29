-- EduManage NG - Financial Service Database Schema
-- V1: Initial schema (Currency: NGN - Nigerian Naira)

CREATE TABLE IF NOT EXISTS fee_structures (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    school_id BIGINT NOT NULL,
    session_id BIGINT,
    session_name VARCHAR(20),
    level ENUM('CRECHE','NURSERY','PRIMARY','SECONDARY'),
    class_name VARCHAR(100),
    fee_type ENUM('TUITION','DEVELOPMENT_LEVY','SPORTS','LIBRARY','EXAM','BUS','UNIFORM','MISCELLANEOUS') NOT NULL,
    fee_description VARCHAR(300),
    amount DECIMAL(12,2) NOT NULL COMMENT 'Amount in NGN (Naira)',
    term ENUM('FIRST_TERM','SECOND_TERM','THIRD_TERM','ANNUAL'),
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_fee_school (school_id),
    INDEX idx_fee_session (session_id),
    INDEX idx_fee_level (level)
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference_code VARCHAR(50) NOT NULL UNIQUE,
    paystack_reference VARCHAR(100),
    student_id BIGINT NOT NULL,
    school_id BIGINT NOT NULL,
    fee_structure_id BIGINT,
    fee_description VARCHAR(200),
    amount DECIMAL(12,2) NOT NULL COMMENT 'Amount in NGN',
    amount_paid DECIMAL(12,2) DEFAULT 0.00,
    status ENUM('PENDING','SUCCESS','FAILED','CANCELLED','REFUNDED') NOT NULL DEFAULT 'PENDING',
    payment_method ENUM('PAYSTACK','BANK_TRANSFER','CASH','POS'),
    payment_date DATE,
    payer_name VARCHAR(200),
    payer_phone VARCHAR(20),
    receipt_number VARCHAR(50),
    narration VARCHAR(300),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_payment_student (student_id),
    INDEX idx_payment_school (school_id),
    INDEX idx_payment_status (status),
    INDEX idx_payment_reference (reference_code)
);
