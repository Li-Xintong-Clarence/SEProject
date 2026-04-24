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
    `user_type` VARCHAR(20) DEFAULT 'NORMAL' COMMENT '用户类型：NORMAL=普通用户，STUDENT=学生，SENIOR=长者',
    `registration_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `is_active` BOOLEAN DEFAULT TRUE,
    INDEX idx_username (`username`),
    INDEX idx_role (`role`),
    INDEX idx_user_type (`user_type`)
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

-- =============================================
-- 全国滑板车分布数据（约170辆车）
-- 成都犀浦校区约50辆，其余分散全国
-- =============================================

-- 成都犀浦校区附近（约50辆，分布在3km范围内）
INSERT INTO `scooters` (`scooter_number`, `status`, `battery_level`, `latitude`, `longitude`, `location`) VALUES
-- 犀浦校区核心区
('SC001', 'AVAILABLE', 95.00, 30.7528, 103.9305, '西南交通大学南门'),
('SC002', 'AVAILABLE', 88.50, 30.7535, 103.9312, '犀浦校区图书馆'),
('SC003', 'IN_USE', 72.00, 30.7518, 103.9298, '犀浦校区体育馆'),
('SC004', 'AVAILABLE', 100.00, 30.7542, 103.9320, '犀浦校区东门'),
('SC005', 'MAINTENANCE', 45.00, 30.7505, 103.9280, '犀浦校区维修站'),
('SC006', 'AVAILABLE', 92.00, 30.7558, 103.9335, '犀浦地铁站A口'),
('SC007', 'AVAILABLE', 85.00, 30.7562, 103.9342, '犀浦地铁站B口'),
('SC008', 'IN_USE', 68.00, 30.7548, 103.9328, '犀浦地铁站商业街'),
('SC009', 'AVAILABLE', 98.00, 30.7585, 103.9360, '红高路附近'),
('SC010', 'AVAILABLE', 76.00, 30.7498, 103.9275, '犀浦校区北门'),
('SC011', 'AVAILABLE', 90.00, 30.7520, 103.9318, '犀浦校区教学楼区'),
('SC012', 'AVAILABLE', 82.00, 30.7508, 103.9295, '犀浦校区食堂附近'),
('SC013', 'IN_USE', 65.00, 30.7572, 103.9355, '犀浦校区游泳馆'),
('SC014', 'AVAILABLE', 94.00, 30.7538, 103.9308, '犀浦校区宿舍区'),
('SC015', 'AVAILABLE', 79.00, 30.7495, 103.9268, '犀浦校区操场'),
('SC016', 'AVAILABLE', 91.00, 30.7532, 103.9325, '犀浦校区商业街'),
('SC017', 'IN_USE', 77.00, 30.7515, 103.9288, '犀浦校区实验楼'),
('SC018', 'AVAILABLE', 86.00, 30.7550, 103.9330, '犀浦校区南广场'),
('SC019', 'AVAILABLE', 93.00, 30.7502, 103.9292, '犀浦校区行政楼'),
('SC020', 'AVAILABLE', 89.00, 30.7565, 103.9348, '犀浦校区停车场'),
-- 犀浦校区外围延伸
('SC021', 'AVAILABLE', 97.00, 30.7590, 103.9370, '百草路地铁站'),
('SC022', 'AVAILABLE', 84.00, 30.7480, 103.9255, '红光镇附近'),
('SC023', 'IN_USE', 71.00, 30.7575, 103.9352, '成都后花园附近'),
('SC024', 'AVAILABLE', 96.00, 30.7545, 103.9315, '犀浦校区东门对面'),
('SC025', 'AVAILABLE', 88.00, 30.7525, 103.9298, '犀浦校区校医院'),
('SC026', 'AVAILABLE', 92.00, 30.7595, 103.9368, '学府路一段'),
('SC027', 'AVAILABLE', 85.00, 30.7475, 103.9262, '蜀都大道郫都段'),
('SC028', 'IN_USE', 69.00, 30.7555, 103.9338, '华邑阳光小区附近'),
('SC029', 'AVAILABLE', 90.00, 30.7510, 103.9285, '犀浦校区艺术中心'),
('SC030', 'AVAILABLE', 94.00, 30.7580, 103.9358, '犀安路'),
('SC031', 'AVAILABLE', 87.00, 30.7490, 103.9272, '郫都区体育馆'),
('SC032', 'AVAILABLE', 91.00, 30.7568, 103.9345, '蓝光幸福满庭'),
('SC033', 'IN_USE', 75.00, 30.7508, 103.9300, '犀浦校区图书馆东门'),
('SC034', 'AVAILABLE', 89.00, 30.7578, 103.9355, '成都纺织高等专科学校'),
('SC035', 'AVAILABLE', 95.00, 30.7485, 103.9268, '犀池六路'),
('SC036', 'AVAILABLE', 83.00, 30.7540, 103.9322, '犀浦校区综合楼'),
('SC037', 'AVAILABLE', 98.00, 30.7592, 103.9372, '犀团路'),
('SC038', 'IN_USE', 67.00, 30.7512, 103.9288, '西南交大西门附近'),
('SC039', 'AVAILABLE', 92.00, 30.7562, 103.9340, '龙梓路'),
('SC040', 'AVAILABLE', 86.00, 30.7478, 103.9258, '红光广场'),
('SC041', 'AVAILABLE', 90.00, 30.7538, 103.9318, '犀浦校区体育馆北门'),
('SC042', 'AVAILABLE', 88.00, 30.7588, 103.9365, '成都外国语学院'),
('SC043', 'AVAILABLE', 94.00, 30.7498, 103.9278, '郫都区文化馆'),
('SC044', 'IN_USE', 73.00, 30.7552, 103.9332, '犀浦校区学生服务中心'),
('SC045', 'AVAILABLE', 96.00, 30.7522, 103.9295, '犀浦校区学术交流中心'),
('SC046', 'AVAILABLE', 85.00, 30.7575, 103.9352, '学府路二段'),
('SC047', 'AVAILABLE', 91.00, 30.7482, 103.9265, '成灌东路'),
('SC048', 'AVAILABLE', 89.00, 30.7545, 103.9312, '犀浦校区机械学院'),
('SC049', 'AVAILABLE', 93.00, 30.7598, 103.9375, '蜀源路'),
('SC050', 'AVAILABLE', 87.00, 30.7505, 103.9282, '犀浦校区信息学院'),
-- 成都其他区域（约20辆）
('SC051', 'AVAILABLE', 95.00, 30.5728, 104.0665, '成都天府广场'),
('SC052', 'AVAILABLE', 88.00, 30.6580, 104.0650, '成都春熙路'),
('SC053', 'IN_USE', 72.00, 30.6630, 104.0720, '成都太古里'),
('SC054', 'AVAILABLE', 91.00, 30.5720, 104.0620, '成都人民公园'),
('SC055', 'AVAILABLE', 86.00, 30.5725, 104.0750, '成都宽窄巷子'),
('SC056', 'AVAILABLE', 93.00, 30.5450, 104.0420, '成都武侯祠'),
('SC057', 'AVAILABLE', 89.00, 30.5720, 104.0550, '成都天府广场东'),
('SC058', 'IN_USE', 77.00, 30.6635, 104.0680, '成都IFS'),
('SC059', 'AVAILABLE', 92.00, 30.5715, 104.0580, '成都体育学院'),
('SC060', 'AVAILABLE', 85.00, 30.5480, 104.0380, '成都锦里'),
('SC061', 'AVAILABLE', 90.00, 30.6620, 104.0700, '成都红星路'),
('SC062', 'AVAILABLE', 88.00, 30.5740, 104.0680, '成都骡马市'),
('SC063', 'IN_USE', 74.00, 30.5460, 104.0500, '成都浆洗街'),
('SC064', 'AVAILABLE', 94.00, 30.5780, 104.0520, '成都少城路'),
('SC065', 'AVAILABLE', 87.00, 30.6640, 104.0750, '成都大慈寺'),
('SC066', 'AVAILABLE', 96.00, 30.5465, 104.0550, '成都洗面桥'),
('SC067', 'AVAILABLE', 91.00, 30.6610, 104.0620, '成都红星路广场'),
('SC068', 'AVAILABLE', 89.00, 30.5735, 104.0600, '成都蜀都大道'),
('SC069', 'IN_USE', 71.00, 30.5490, 104.0450, '成都武侯祠博物馆'),
('SC070', 'AVAILABLE', 93.00, 30.5760, 104.0540, '成都金河路'),
-- 北京（约15辆）
('SC071', 'AVAILABLE', 95.00, 39.9042, 116.4074, '北京天安门'),
('SC072', 'AVAILABLE', 88.00, 39.9080, 116.4120, '北京王府井'),
('SC073', 'IN_USE', 72.00, 39.9065, 116.3970, '北京故宫'),
('SC074', 'AVAILABLE', 91.00, 39.9120, 116.4080, '北京东单'),
('SC075', 'AVAILABLE', 86.00, 39.9020, 116.4180, '北京建国门'),
('SC076', 'AVAILABLE', 93.00, 39.9100, 116.4040, '北京西单'),
('SC077', 'AVAILABLE', 89.00, 39.9140, 116.4150, '北京崇文门'),
('SC078', 'IN_USE', 77.00, 39.9085, 116.4060, '北京前门'),
('SC079', 'AVAILABLE', 92.00, 39.9110, 116.4100, '北京东四'),
('SC080', 'AVAILABLE', 85.00, 39.9070, 116.4200, '北京朝阳门'),
('SC081', 'AVAILABLE', 90.00, 39.9130, 116.4050, '北京景山'),
('SC082', 'AVAILABLE', 88.00, 39.9055, 116.4150, '北京灯市口'),
('SC083', 'IN_USE', 74.00, 39.9090, 116.4020, '北京南池子'),
('SC084', 'AVAILABLE', 94.00, 39.9105, 116.4180, '北京东长安街'),
('SC085', 'AVAILABLE', 87.00, 39.9060, 116.4080, '北京劳动人民文化宫'),
-- 上海（约15辆）
('SC086', 'AVAILABLE', 95.00, 31.2304, 121.4737, '上海人民广场'),
('SC087', 'AVAILABLE', 88.00, 31.2280, 121.4800, '上海南京路'),
('SC088', 'IN_USE', 72.00, 31.2350, 121.4750, '上海外滩'),
('SC089', 'AVAILABLE', 91.00, 31.2250, 121.4900, '上海淮海路'),
('SC090', 'AVAILABLE', 86.00, 31.2320, 121.4680, '上海静安寺'),
('SC091', 'AVAILABLE', 93.00, 31.2200, 121.4850, '上海新天地'),
('SC092', 'AVAILABLE', 89.00, 31.2340, 121.4780, '上海黄浦公园'),
('SC093', 'IN_USE', 77.00, 31.2270, 121.4950, '上海陆家嘴'),
('SC094', 'AVAILABLE', 92.00, 31.2295, 121.4720, '上海人民公园'),
('SC095', 'AVAILABLE', 85.00, 31.2230, 121.4980, '上海世纪公园'),
('SC096', 'AVAILABLE', 90.00, 31.2360, 121.4830, '上海美术馆'),
('SC097', 'AVAILABLE', 88.00, 31.2240, 121.4760, '上海大世界'),
('SC098', 'IN_USE', 74.00, 31.2300, 121.4880, '上海豫园'),
('SC099', 'AVAILABLE', 94.00, 31.2285, 121.4700, '上海自然博物馆'),
('SC100', 'AVAILABLE', 87.00, 31.2325, 121.4930, '上海东方明珠'),
-- 广州（约10辆）
('SC101', 'AVAILABLE', 95.00, 23.1291, 113.2644, '广州天河城'),
('SC102', 'AVAILABLE', 88.00, 23.1215, 113.2580, '广州北京路'),
('SC103', 'IN_USE', 72.00, 23.1195, 113.3250, '广州珠江新城'),
('SC104', 'AVAILABLE', 91.00, 23.1300, 113.2650, '广州体育西路'),
('SC105', 'AVAILABLE', 86.00, 23.1280, 113.2500, '广州公园前'),
('SC106', 'AVAILABLE', 93.00, 23.1350, 113.2750, '广州天环广场'),
('SC107', 'AVAILABLE', 89.00, 23.1180, 113.3200, '广州猎德'),
('SC108', 'IN_USE', 77.00, 23.1260, 113.2620, '广州中华广场'),
('SC109', 'AVAILABLE', 92.00, 23.1320, 113.2700, '广州正佳广场'),
('SC110', 'AVAILABLE', 85.00, 23.1240, 113.2550, '广州动漫星城'),
-- 深圳（约10辆）
('SC111', 'AVAILABLE', 95.00, 22.5431, 114.0579, '深圳华强北'),
('SC112', 'AVAILABLE', 88.00, 22.5480, 114.0620, '深圳东门'),
('SC113', 'IN_USE', 72.00, 22.5345, 114.0495, '深圳福田CBD'),
('SC114', 'AVAILABLE', 91.00, 22.5500, 114.0680, '深圳会展中心'),
('SC115', 'AVAILABLE', 86.00, 22.5400, 114.0550, '深圳科学馆'),
('SC116', 'AVAILABLE', 93.00, 22.5350, 114.0700, '深圳平安金融中心'),
('SC117', 'AVAILABLE', 89.00, 22.5460, 114.0600, '深圳大剧院'),
('SC118', 'IN_USE', 77.00, 22.5380, 114.0530, '深圳购物公园'),
('SC119', 'AVAILABLE', 92.00, 22.5510, 114.0650, '深圳卓越世纪中心'),
('SC120', 'AVAILABLE', 85.00, 22.5320, 114.0480, '深圳中心城'),
-- 杭州（约8辆）
('SC121', 'AVAILABLE', 95.00, 30.2480, 120.1580, '杭州西湖断桥'),
('SC122', 'AVAILABLE', 88.00, 30.2530, 120.1520, '杭州龙翔桥'),
('SC123', 'IN_USE', 72.00, 30.2460, 120.1650, '杭州湖滨银泰'),
('SC124', 'AVAILABLE', 91.00, 30.2560, 120.1500, '杭州武林广场'),
('SC125', 'AVAILABLE', 86.00, 30.2440, 120.1580, '杭州柳浪闻莺'),
('SC126', 'AVAILABLE', 93.00, 30.2510, 120.1550, '杭州东坡路'),
('SC127', 'AVAILABLE', 89.00, 30.2580, 120.1600, '杭州平海路'),
('SC128', 'IN_USE', 77.00, 30.2490, 120.1620, '杭州湖滨路'),
-- 武汉（约8辆）
('SC129', 'AVAILABLE', 95.00, 30.5855, 114.3055, '武汉江汉路'),
('SC130', 'AVAILABLE', 88.00, 30.5720, 114.2710, '武汉光谷广场'),
('SC131', 'IN_USE', 72.00, 30.5780, 114.2950, '武汉楚河汉街'),
('SC132', 'AVAILABLE', 91.00, 30.5650, 114.2800, '武汉街道口'),
('SC133', 'AVAILABLE', 86.00, 30.5880, 114.3100, '武汉汉正街'),
('SC134', 'AVAILABLE', 93.00, 30.5750, 114.2880, '武汉中南路'),
('SC135', 'AVAILABLE', 89.00, 30.5800, 114.3020, '武汉武广商圈'),
('SC136', 'IN_USE', 77.00, 30.5680, 114.2750, '武汉广埠屯'),
-- 西安（约8辆）
('SC137', 'AVAILABLE', 95.00, 34.2655, 108.9540, '西安钟楼'),
('SC138', 'AVAILABLE', 88.00, 34.2580, 108.9570, '西安回民街'),
('SC139', 'IN_USE', 72.00, 34.2640, 108.9430, '西安大雁塔'),
('SC140', 'AVAILABLE', 91.00, 34.2700, 108.9600, '西安北大街'),
('SC141', 'AVAILABLE', 86.00, 34.2620, 108.9500, '西安南门'),
('SC142', 'AVAILABLE', 93.00, 34.2680, 108.9450, '西安小寨'),
('SC143', 'AVAILABLE', 89.00, 34.2750, 108.9520, '西安西大街'),
('SC144', 'IN_USE', 77.00, 34.2600, 108.9480, '西安骡马市'),
-- 南京（约8辆）
('SC145', 'AVAILABLE', 95.00, 32.0602, 118.7969, '南京新街口'),
('SC146', 'AVAILABLE', 88.00, 32.0580, 118.7900, '南京夫子庙'),
('SC147', 'IN_USE', 72.00, 32.0620, 118.7800, '南京鼓楼广场'),
('SC148', 'AVAILABLE', 91.00, 32.0550, 118.8020, '南京中山陵'),
('SC149', 'AVAILABLE', 86.00, 32.0650, 118.7850, '南京玄武湖'),
('SC150', 'AVAILABLE', 93.00, 32.0590, 118.7950, '南京珠江路'),
('SC151', 'AVAILABLE', 89.00, 32.0630, 118.7880, '南京湖南路'),
('SC152', 'IN_USE', 77.00, 32.0570, 118.7980, '南京太平北路'),
-- 重庆（约5辆）
('SC153', 'AVAILABLE', 95.00, 29.5630, 106.5510, '重庆解放碑'),
('SC154', 'AVAILABLE', 88.00, 29.5580, 106.5780, '重庆观音桥'),
('SC155', 'IN_USE', 72.00, 29.5590, 106.5350, '重庆洪崖洞'),
('SC156', 'AVAILABLE', 91.00, 29.5650, 106.5650, '重庆沙坪坝'),
('SC157', 'AVAILABLE', 86.00, 29.5610, 106.5550, '重庆临江门'),
-- 天津（约5辆）
('SC158', 'AVAILABLE', 95.00, 39.1256, 117.1909, '天津古文化街'),
('SC159', 'AVAILABLE', 88.00, 39.1280, 117.1980, '天津意式风情区'),
('SC160', 'IN_USE', 72.00, 39.1350, 117.2050, '天津天津之眼'),
('SC161', 'AVAILABLE', 91.00, 39.1320, 117.2020, '天津大悦城'),
('SC162', 'AVAILABLE', 86.00, 39.1380, 117.2100, '天津南市食品街'),
-- 苏州（约5辆）
('SC163', 'AVAILABLE', 95.00, 31.2990, 120.5850, '苏州观前街'),
('SC164', 'AVAILABLE', 88.00, 31.3220, 120.6320, '苏州金鸡湖'),
('SC165', 'IN_USE', 72.00, 31.3080, 120.6180, '苏州苏州中心'),
('SC166', 'AVAILABLE', 91.00, 31.3150, 120.6280, '苏州圆融时代广场'),
('SC167', 'AVAILABLE', 86.00, 31.3250, 120.6350, '苏州摩天轮公园'),
-- 郑州（约5辆）
('SC168', 'AVAILABLE', 95.00, 34.7580, 113.6250, '郑州二七广场'),
('SC169', 'AVAILABLE', 88.00, 34.7650, 113.6320, '郑州大卫城'),
('SC170', 'IN_USE', 72.00, 34.7620, 113.6280, '郑州人民公园'),
('SC171', 'AVAILABLE', 91.00, 34.7680, 113.6350, '郑州丹尼斯大卫城'),
('SC172', 'AVAILABLE', 86.00, 34.7550, 113.6220, '郑州德化街')
ON DUPLICATE KEY UPDATE status = VALUES(status), battery_level = VALUES(battery_level), latitude = VALUES(latitude), longitude = VALUES(longitude), location = VALUES(location);
