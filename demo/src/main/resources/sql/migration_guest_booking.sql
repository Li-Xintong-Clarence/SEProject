-- ID7: 未注册用户(Guest)预订支持
-- 为 booking 表添加 guest 相关字段

-- 添加预订类型字段
ALTER TABLE booking ADD COLUMN IF NOT EXISTS booking_type VARCHAR(20) DEFAULT 'REGISTERED';

-- 添加访客姓名字段
ALTER TABLE booking ADD COLUMN IF NOT EXISTS guest_name VARCHAR(100);

-- 添加访客电话字段
ALTER TABLE booking ADD COLUMN IF NOT EXISTS guest_phone VARCHAR(20);

-- 添加访客邮箱字段
ALTER TABLE booking ADD COLUMN IF NOT EXISTS guest_email VARCHAR(100);

-- 如果之前添加过但失败，可以尝试单独执行（去掉 IF NOT EXISTS）
-- ALTER TABLE booking ADD COLUMN booking_type VARCHAR(20) DEFAULT 'REGISTERED';
-- ALTER TABLE booking ADD COLUMN guest_name VARCHAR(100);
-- ALTER TABLE booking ADD COLUMN guest_phone VARCHAR(20);
-- ALTER TABLE booking ADD COLUMN guest_email VARCHAR(100);
