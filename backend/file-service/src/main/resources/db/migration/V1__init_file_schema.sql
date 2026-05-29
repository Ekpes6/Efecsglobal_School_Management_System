-- EduManage NG - File Service Database Schema
-- V1: Initial schema

CREATE TABLE IF NOT EXISTS file_metadata (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    school_id BIGINT,
    uploader_user_id BIGINT,
    file_name VARCHAR(255) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100),
    file_size BIGINT,
    file_path VARCHAR(500) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    is_public BOOLEAN DEFAULT FALSE,
    uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_file_school (school_id),
    INDEX idx_file_entity (entity_type, entity_id),
    INDEX idx_file_uploader (uploader_user_id)
);
