-- 折扣系统数据库迁移脚本
-- 为 users 表添加 user_type 字段支持学生和长者折扣
-- 日期: 2026-04-12

USE scooter_rental;

-- 添加 user_type 字段
ALTER TABLE `users`
ADD COLUMN `user_type` VARCHAR(20) DEFAULT 'NORMAL' COMMENT '用户类型：NORMAL=普通用户，STUDENT=学生，SENIOR=长者'
AFTER `role`;

-- 创建索引
ALTER TABLE `users`
ADD INDEX idx_user_type (`user_type`);

-- 将现有管理员设置为 NORMAL 类型
UPDATE `users` SET `user_type` = 'NORMAL' WHERE `role` = 'ADMIN';

-- 将现有普通用户设置为 NORMAL 类型
UPDATE `users` SET `user_type` = 'NORMAL' WHERE `role` = 'CUSTOMER' AND `user_type` IS NULL;

-- 验证字段添加成功
SELECT id, username, role, user_type FROM `users` LIMIT 10;
