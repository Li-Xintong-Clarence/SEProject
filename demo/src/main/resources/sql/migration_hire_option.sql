-- Migration: Backlog ID4, ID5 - Hire options + Booking table
-- Run this if database already exists (e.g. after pulling backend branch).

USE scooter_rental;

-- ID4: Hire options table
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

INSERT IGNORE INTO `hire_option` (`code`, `label`, `duration_minutes`, `price`, `display_order`) VALUES
('1HR', '1 hour', 60, 5.00, 1),
('4HR', '4 hours', 240, 15.00, 2),
('1DAY', '1 day', 1440, 25.00, 3),
('1WEEK', '1 week', 10080, 80.00, 4);

-- ID5: Booking table (replace old rental table)
CREATE TABLE IF NOT EXISTS `booking` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `scooter_id` BIGINT NOT NULL,
    `hire_option_code` VARCHAR(20) NOT NULL,
    `start_time` DATETIME NOT NULL,
    `scheduled_end_time` DATETIME,
    `actual_end_time` DATETIME,
    `actual_duration_minutes` INT,
    `cost` DECIMAL(10,2),
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`scooter_id`) REFERENCES `scooter`(`id`),
    INDEX idx_user_id (`user_id`),
    INDEX idx_scooter_id (`scooter_id`),
    INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
