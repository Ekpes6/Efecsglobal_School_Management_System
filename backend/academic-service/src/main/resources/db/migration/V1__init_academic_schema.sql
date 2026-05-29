-- EduManage NG - Academic Service Database Schema
-- V1: Initial schema

CREATE TABLE IF NOT EXISTS subjects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    school_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    subject_code VARCHAR(20),
    description VARCHAR(300),
    academic_level ENUM('CRECHE','NURSERY','PRIMARY','SECONDARY'),
    teacher_id BIGINT,
    is_compulsory BOOLEAN DEFAULT TRUE,
    class_id BIGINT,
    INDEX idx_subject_school (school_id),
    INDEX idx_subject_class (class_id),
    INDEX idx_subject_level (academic_level)
);

CREATE TABLE IF NOT EXISTS results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    school_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    subject_name VARCHAR(150),
    class_id BIGINT,
    class_name VARCHAR(100),
    session_id BIGINT NOT NULL,
    session_name VARCHAR(20),
    term ENUM('FIRST_TERM','SECOND_TERM','THIRD_TERM') NOT NULL,
    ca1_score DECIMAL(5,2),
    ca2_score DECIMAL(5,2),
    ca3_score DECIMAL(5,2),
    exam_score DECIMAL(5,2),
    total_score DECIMAL(5,2),
    grade VARCHAR(5),
    grade_remark VARCHAR(20),
    position_in_class INT,
    teacher_remark VARCHAR(300),
    is_published BOOLEAN DEFAULT FALSE,
    teacher_id BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_student_subject_session_term (student_id, subject_id, session_id, term),
    INDEX idx_result_student (student_id),
    INDEX idx_result_school (school_id),
    INDEX idx_result_session (session_id),
    INDEX idx_result_published (is_published)
);
