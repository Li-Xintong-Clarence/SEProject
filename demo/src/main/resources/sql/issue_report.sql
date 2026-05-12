-- 问题报告表
CREATE TABLE IF NOT EXISTS `issue_report` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `scooter_id` BIGINT NOT NULL,
    `description` TEXT NOT NULL,
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '问题状态：PENDING（待处理）, IN_PROGRESS（处理中）, RESOLVED（已解决）',
    `priority` VARCHAR(20) DEFAULT 'NORMAL' COMMENT '优先级：LOW（低）, NORMAL（普通）, HIGH（高）',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `resolved_at` DATETIME,
    `admin_feedback` TEXT COMMENT '管理员反馈',
    INDEX idx_user_id (`user_id`),
    INDEX idx_scooter_id (`scooter_id`),
    INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
