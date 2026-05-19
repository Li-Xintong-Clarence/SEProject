-- 超时费用配置表
-- 用于配置超过租赁时长后的超时费用

CREATE TABLE IF NOT EXISTS overtime_fee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID（主键）',
    hire_option VARCHAR(20) NOT NULL COMMENT '关联的租期选项代码（如 1hr, 4hr, 1day, 1week）',
    hire_option_name VARCHAR(50) NOT NULL COMMENT '租期描述',
    fee_type VARCHAR(20) NOT NULL DEFAULT 'HOURLY' COMMENT '费率类型：HOURLY（按小时）, FIXED（固定金额/超时）',
    fee DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '超时费用',
    max_overtime_minutes INT DEFAULT NULL COMMENT '最大超时限制（分钟），NULL表示不限制',
    enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：1启用，0禁用',
    UNIQUE KEY uk_hire_option (hire_option)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='超时费用配置表';

-- 插入默认超时费用配置
INSERT INTO overtime_fee (hire_option, hire_option_name, fee_type, fee, max_overtime_minutes, enabled) VALUES
('1hr', '1小时租赁', 'HOURLY', 5.00, 60, 1),
('4hr', '4小时租赁', 'HOURLY', 3.00, 120, 1),
('1day', '1天租赁', 'HOURLY', 2.00, 240, 1),
('1week', '1周租赁', 'FIXED', 50.00, NULL, 1)
ON DUPLICATE KEY UPDATE hire_option_name = VALUES(hire_option_name);
