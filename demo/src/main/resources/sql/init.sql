-- 电动车租赁系统数据库初始化脚本
-- 包含所有表的创建和测试数据

-- 创建数据库
CREATE DATABASE IF NOT EXISTS scooter_rental CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE scooter_rental;

-- =============================================
-- 用户表 (users)
-- =============================================
CREATE TABLE IF NOT EXISTS `users` (
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

-- =============================================
-- 电动车表 (scooters)
-- =============================================
CREATE TABLE IF NOT EXISTS `scooters` (
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

-- =============================================
-- 预订表 (booking) — 与后端 Booking 实体、BookingMapper 一致
-- =============================================
CREATE TABLE IF NOT EXISTS `booking` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `scooter_id` BIGINT NOT NULL,
    `hire_option` VARCHAR(20) NOT NULL COMMENT '1hr, 4hr, 1day, 1week',
    `start_time` DATETIME,
    `end_time` DATETIME,
    `total_cost` DECIMAL(10,2),
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING, PAID, ACTIVE, COMPLETED, CANCELLED',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `confirmation_code` VARCHAR(50) UNIQUE COMMENT '预订确认码',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`scooter_id`) REFERENCES `scooters`(`id`),
    INDEX idx_user_id (`user_id`),
    INDEX idx_scooter_id (`scooter_id`),
    INDEX idx_status (`status`),
    INDEX idx_confirmation_code (`confirmation_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 反馈表 (feedback)
-- =============================================
CREATE TABLE IF NOT EXISTS `feedback` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `booking_id` BIGINT,
    `description` TEXT NOT NULL,
    `status` VARCHAR(20) DEFAULT 'OPEN' COMMENT 'OPEN, IN_PROGRESS, RESOLVED',
    `priority` VARCHAR(20) DEFAULT 'MEDIUM' COMMENT 'LOW, MEDIUM, HIGH',
    `admin_response` TEXT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `resolved_at` DATETIME,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`booking_id`) REFERENCES `booking`(`id`),
    INDEX idx_status (`status`),
    INDEX idx_priority (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 价格配置表 (pricing)
-- =============================================
CREATE TABLE IF NOT EXISTS `pricing` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `hire_option` VARCHAR(20) NOT NULL COMMENT '1hr, 4hr, 1day, 1week',
    `price` DECIMAL(10,2) NOT NULL,
    `description` VARCHAR(255),
    UNIQUE INDEX idx_hire_option (`hire_option`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 银行卡信息表 (card) — README 3.10 可选接口
-- =============================================
CREATE TABLE IF NOT EXISTS `card` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `card_number` VARCHAR(255),
    `card_holder` VARCHAR(100),
    `expiry_date` VARCHAR(7),
    `cvv` VARCHAR(10),
    `is_default` TINYINT(1) DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 租用选项表 (hire_option) — HireOptionMapper.xml 依赖此表；/api/hire-options
-- =============================================
CREATE TABLE IF NOT EXISTS `hire_option` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(20) NOT NULL UNIQUE,
    `label` VARCHAR(50) NOT NULL,
    `duration_minutes` INT NOT NULL,
    `price` DECIMAL(10,2) NOT NULL,
    `display_order` INT DEFAULT 0,
    `is_active` BOOLEAN DEFAULT TRUE,
    INDEX idx_code (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `hire_option` (`code`, `label`, `duration_minutes`, `price`, `display_order`) VALUES
('1HR', '1 hour', 60, 5.00, 1),
('4HR', '4 hours', 240, 15.00, 2),
('1DAY', '1 day', 1440, 25.00, 3),
('1WEEK', '1 week', 10080, 80.00, 4)
ON DUPLICATE KEY UPDATE label = VALUES(label);

-- =============================================
-- 插入测试数据
-- =============================================

-- 插入默认价格配置
INSERT INTO `pricing` (`hire_option`, `price`, `description`) VALUES
('1hr', 3.00, '1小时租赁'),
('4hr', 10.00, '4小时租赁'),
('1day', 20.00, '1天租赁'),
('1week', 100.00, '1周租赁')
ON DUPLICATE KEY UPDATE price = VALUES(price);

-- 插入测试管理员用户 (密码: admin123)
-- BCrypt加密后的哈希
INSERT INTO `users` (`username`, `password`, `email`, `phone`, `role`) VALUES
('admin', '$2b$10$lxJRS0vBC8BhLyLX1hImA.9T/qdvArw8pyOaTn.fH.7iN.hiqPiBO', 'admin@scooter.com', '1234567890', 'ADMIN')
ON DUPLICATE KEY UPDATE password = VALUES(password);

-- 插入测试普通用户 (密码: user123)
INSERT INTO `users` (`username`, `password`, `email`, `phone`, `role`) VALUES
('testuser', '$2b$10$ek8H96g7uwV.YbzgSuFB1eSe6r4s442vrTIjBEYmjKbBRwny0RuQa', 'user@test.com', '0987654321', 'CUSTOMER')
ON DUPLICATE KEY UPDATE password = VALUES(password);

-- 插入测试滑板车数据
INSERT INTO `scooters` (`scooter_number`, `status`, `battery_level`, `latitude`, `longitude`, `location`) VALUES
('SC001', 'AVAILABLE', 95.00, 53.7987, -1.5426, 'Leeds City Centre'),
('SC002', 'AVAILABLE', 88.50, 53.8007, -1.5456, 'Leeds Railway Station'),
('SC003', 'IN_USE', 72.00, 53.7967, -1.5406, 'Leeds University'),
('SC004', 'AVAILABLE', 100.00, 53.8027, -1.5486, 'Leeds Shopping Centre'),
('SC005', 'MAINTENANCE', 45.00, 53.7947, -1.5386, 'Service Center')
ON DUPLICATE KEY UPDATE status = VALUES(status);
