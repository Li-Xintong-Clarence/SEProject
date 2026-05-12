-- 创建 depot 服务点表
CREATE TABLE IF NOT EXISTS depot (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    depot_number VARCHAR(50),
    name VARCHAR(100),
    latitude DECIMAL(10, 6),
    longitude DECIMAL(10, 6),
    address VARCHAR(200),
    capacity INT DEFAULT 10,
    current_stock INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

-- 修改 scooters 表，添加 depot_id 字段
ALTER TABLE scooters ADD COLUMN depot_id BIGINT;

-- 修改 booking 表，添加服务点字段
ALTER TABLE booking ADD COLUMN start_depot_id BIGINT;
ALTER TABLE booking ADD COLUMN end_depot_id BIGINT;

-- 如果 current_stock 列不存在，添加它
ALTER TABLE depot ADD COLUMN IF NOT EXISTS current_stock INT DEFAULT 0;

-- 添加测试数据（如果 depot 表为空）
INSERT IGNORE INTO depot (depot_number, name, latitude, longitude, address, capacity) VALUES
('D001', '服务点 A（地铁站A口）', 30.746, 103.922, '地铁1号线科学城站A口', 10),
('D002', '服务点 B（商业中心）', 30.754, 103.936, '天府新区商业中心', 15),
('D003', '服务点 C（公园入口）', 30.758, 103.915, '兴隆湖公园南入口', 8),
('D004', '服务点 D（办公楼）', 30.739, 103.944, '天府软件园G区', 12),
('D005', '服务点 E（学校门口）', 30.765, 103.928, '四川大学锦江学院', 10);

-- 更新滑板车绑定到服务点
UPDATE scooters SET depot_id = 1 WHERE id <= 5;
UPDATE scooters SET depot_id = 2 WHERE id > 5 AND id <= 10;
UPDATE scooters SET depot_id = 3 WHERE id > 10 AND id <= 15;
UPDATE scooters SET depot_id = 4 WHERE id > 15 AND id <= 20;
UPDATE scooters SET depot_id = 5 WHERE id > 20;
