-- 创建数据库
CREATE DATABASE IF NOT EXISTS scooter_rental CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE scooter_rental;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `email` VARCHAR(100),
    `phone` VARCHAR(20),
    `role` VARCHAR(20) DEFAULT 'CUSTOMER',
    `registration_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `is_active` BOOLEAN DEFAULT TRUE,
    INDEX idx_username (`username`),
    INDEX idx_role (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 滑板车表
CREATE TABLE IF NOT EXISTS `scooter` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `scooter_number` VARCHAR(50) NOT NULL UNIQUE,
    `status` VARCHAR(20) DEFAULT 'AVAILABLE',
    `battery_level` DECIMAL(5,2) DEFAULT 100.00,
    `latitude` DOUBLE,
    `longitude` DOUBLE,
    `location` VARCHAR(255),
    `last_maintenance_date` DATETIME,
    INDEX idx_status (`status`),
    INDEX idx_scooter_number (`scooter_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 租赁表
CREATE TABLE IF NOT EXISTS `rental` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `scooter_id` BIGINT NOT NULL,
    `start_time` DATETIME NOT NULL,
    `end_time` DATETIME,
    `actual_duration_minutes` INT,
    `cost` DECIMAL(10,2),
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    `rental_type` VARCHAR(20) DEFAULT 'ONE_TIME',
    `scheduled_end_time` DATETIME,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`scooter_id`) REFERENCES `scooter`(`id`),
    INDEX idx_user_id (`user_id`),
    INDEX idx_scooter_id (`scooter_id`),
    INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 问题报告表
CREATE TABLE IF NOT EXISTS `issue_report` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `scooter_id` BIGINT,
    `description` TEXT NOT NULL,
    `status` VARCHAR(20) DEFAULT 'PENDING',
    `priority` VARCHAR(20) DEFAULT 'NORMAL',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `resolved_at` DATETIME,
    `admin_feedback` TEXT,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`scooter_id`) REFERENCES `scooter`(`id`),
    INDEX idx_status (`status`),
    INDEX idx_priority (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 价格配置表
CREATE TABLE IF NOT EXISTS `price_config` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `rental_type` VARCHAR(20) NOT NULL,
    `price_per_minute` DECIMAL(10,2) NOT NULL,
    `weekly_discount` DECIMAL(5,2) DEFAULT 0,
    `monthly_discount` DECIMAL(5,2) DEFAULT 0,
    `is_active` BOOLEAN DEFAULT TRUE,
    UNIQUE INDEX idx_rental_type (`rental_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入默认价格配置
INSERT INTO `price_config` (`rental_type`, `price_per_minute`, `weekly_discount`, `monthly_discount`) VALUES
('ONE_TIME', 0.05, 0.10, 0.20),
('WEEKLY', 0.04, 0.15, 0.25),
('MONTHLY', 0.03, 0.20, 0.30);

-- 插入测试管理员用户 (密码: admin123)
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@scooter.com', '1234567890', 'ADMIN');

-- 插入测试普通用户 (密码: user123)
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `role`) VALUES
('testuser', '$2a$10$YQH4x5k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'user@test.com', '0987654321', 'CUSTOMER');

-- 插入测试滑板车数据
INSERT INTO `scooter` (`scooter_number`, `status`, `battery_level`, `latitude`, `longitude`, `location`) VALUES
('SC001', 'AVAILABLE', 95.00, 53.7987, -1.5426, 'Leeds City Centre'),
('SC002', 'AVAILABLE', 88.50, 53.8007, -1.5456, 'Leeds Railway Station'),
('SC003', 'IN_USE', 72.00, 53.7967, -1.5406, 'Leeds University'),
('SC004', 'AVAILABLE', 100.00, 53.8027, -1.5486, 'Leeds Shopping Centre'),
('SC005', 'MAINTENANCE', 45.00, 53.7947, -1.5386, 'Service Center');
