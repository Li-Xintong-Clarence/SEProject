package com.example.demo.config;

import com.example.demo.entity.Hotspot;
import com.example.demo.mapper.HotspotMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 数据库初始化配置
 * 启动时自动创建 depot 表和更新字段
 */
@Component
public class DatabaseInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Autowired(required = false)
    private HotspotMapper hotspotMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (jdbcTemplate == null) {
            logger.warn("JdbcTemplate 未注入，跳过数据库初始化");
            return;
        }

        try {
            logger.info("========== 开始检查数据库结构 ==========");

            // 1. 创建 depot 表（如果不存在）
            createDepotTableIfNotExists();

            // 2. 创建 hotspot 表（如果不存在）
            createHotspotTableIfNotExists();

            // 3. 创建 overtime_fee 表（如果不存在）
            createOvertimeFeeTableIfNotExists();

            // 4. 更新 scooters 表结构
            addScooterDepotColumn();

            // 5. 更新 booking 表结构
            addBookingDepotColumns();

            // 5. 插入测试数据
            insertTestData();

            // 6. 初始化热门区域
            insertHotspotData();

            // 7. 更新滑板车绑定
            updateScooterBindings();

            logger.info("========== 数据库结构检查完成 ==========");
        } catch (Exception e) {
            logger.error("数据库初始化失败: {}", e.getMessage());
        }
    }

    private void createDepotTableIfNotExists() {
        try {
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_NAME = 'depot'"
            );

            if (tables.isEmpty()) {
                jdbcTemplate.execute("""
                    CREATE TABLE depot (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        depot_number VARCHAR(50),
                        name VARCHAR(100),
                        latitude DECIMAL(10, 6),
                        longitude DECIMAL(10, 6),
                        address VARCHAR(200),
                        capacity INT DEFAULT 10,
                        status VARCHAR(20) DEFAULT 'ACTIVE'
                    )
                """);
                logger.info(" depot 表创建成功");
            } else {
                logger.info(" depot 表已存在，跳过创建");
            }
        } catch (Exception e) {
            logger.warn("创建 depot 表时出错: {}", e.getMessage());
        }
    }

    private void createHotspotTableIfNotExists() {
        try {
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_NAME = 'hotspot'"
            );

            if (tables.isEmpty()) {
                jdbcTemplate.execute("""
                    CREATE TABLE hotspot (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(100),
                        latitude DECIMAL(10, 6),
                        longitude DECIMAL(10, 6),
                        radius INT DEFAULT 300,
                        weight INT DEFAULT 10,
                        status VARCHAR(20) DEFAULT 'ACTIVE',
                        location VARCHAR(200)
                    )
                """);
                logger.info(" hotspot 表创建成功");
            } else {
                logger.info(" hotspot 表已存在，跳过创建");
            }
        } catch (Exception e) {
            logger.warn("创建 hotspot 表时出错: {}", e.getMessage());
        }
    }

    private void createOvertimeFeeTableIfNotExists() {
        try {
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_NAME = 'overtime_fee'"
            );

            if (tables.isEmpty()) {
                jdbcTemplate.execute("""
                    CREATE TABLE overtime_fee (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        hire_option VARCHAR(20) NOT NULL,
                        hire_option_name VARCHAR(50) NOT NULL,
                        fee_type VARCHAR(20) NOT NULL DEFAULT 'HOURLY',
                        fee DECIMAL(10,2) NOT NULL DEFAULT 0,
                        max_overtime_minutes INT DEFAULT NULL,
                        enabled TINYINT(1) NOT NULL DEFAULT 1,
                        UNIQUE KEY uk_hire_option (hire_option)
                    )
                """);
                logger.info(" overtime_fee 表创建成功");
            } else {
                logger.info(" overtime_fee 表已存在，跳过创建");
            }

            // 插入默认数据
            insertOvertimeFeeData();
        } catch (Exception e) {
            logger.warn("创建 overtime_fee 表时出错: {}", e.getMessage());
        }
    }

    private void insertOvertimeFeeData() {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM overtime_fee", Integer.class);
            if (count != null && count == 0) {
                jdbcTemplate.execute("""
                    INSERT INTO overtime_fee (hire_option, hire_option_name, fee_type, fee, max_overtime_minutes, enabled) VALUES
                    ('1hr', '1小时租赁', 'HOURLY', 5.00, 60, 1),
                    ('4hr', '4小时租赁', 'HOURLY', 3.00, 120, 1),
                    ('1day', '1天租赁', 'HOURLY', 2.00, 240, 1),
                    ('1week', '1周租赁', 'FIXED', 50.00, NULL, 1)
                """);
                logger.info(" overtime_fee 默认数据插入成功");
            } else {
                logger.info(" overtime_fee 数据已存在，跳过插入");
            }
        } catch (Exception e) {
            logger.warn("插入 overtime_fee 数据时出错: {}", e.getMessage());
        }
    }

    private void addScooterDepotColumn() {
        try {
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_NAME = 'scooters' AND COLUMN_NAME = 'depot_id'"
            );

            if (columns.isEmpty()) {
                jdbcTemplate.execute("ALTER TABLE scooters ADD COLUMN depot_id BIGINT");
                logger.info(" scooters.depot_id 字段添加成功");
            } else {
                logger.info(" scooters.depot_id 字段已存在，跳过");
            }
        } catch (Exception e) {
            logger.warn("添加 scooters.depot_id 时出错: {}", e.getMessage());
        }
    }

    private void addBookingDepotColumns() {
        try {
            List<Map<String, Object>> startCol = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_NAME = 'booking' AND COLUMN_NAME = 'start_depot_id'"
            );
            if (startCol.isEmpty()) {
                jdbcTemplate.execute("ALTER TABLE booking ADD COLUMN start_depot_id BIGINT");
                logger.info(" booking.start_depot_id 字段添加成功");
            } else {
                logger.info(" booking.start_depot_id 字段已存在，跳过");
            }

            List<Map<String, Object>> endCol = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_NAME = 'booking' AND COLUMN_NAME = 'end_depot_id'"
            );
            if (endCol.isEmpty()) {
                jdbcTemplate.execute("ALTER TABLE booking ADD COLUMN end_depot_id BIGINT");
                logger.info(" booking.end_depot_id 字段添加成功");
            } else {
                logger.info(" booking.end_depot_id 字段已存在，跳过");
            }
        } catch (Exception e) {
            logger.warn("添加 booking 字段时出错: {}", e.getMessage());
        }
    }

    private void insertTestData() {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM depot", Integer.class);
            if (count != null && count == 0) {
                jdbcTemplate.execute("""
                    INSERT INTO depot (depot_number, name, latitude, longitude, address, capacity) VALUES
                    ('D001', '服务点 A（地铁站A口）', 30.746, 103.922, '地铁1号线科学城站A口', 10),
                    ('D002', '服务点 B（商业中心）', 30.754, 103.936, '天府新区商业中心', 15),
                    ('D003', '服务点 C（公园入口）', 30.758, 103.915, '兴隆湖公园南入口', 8),
                    ('D004', '服务点 D（办公楼）', 30.739, 103.944, '天府软件园G区', 12),
                    ('D005', '服务点 E（学校门口）', 30.765, 103.928, '四川大学锦江学院', 10)
                """);
                logger.info(" 服务点测试数据插入成功");
            } else {
                logger.info(" 服务点数据已存在，跳过插入");
            }
        } catch (Exception e) {
            logger.warn("插入测试数据时出错: {}", e.getMessage());
        }
    }

    private void insertHotspotData() {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hotspot", Integer.class);
            if (count != null && count == 0) {
                jdbcTemplate.execute("""
                    INSERT INTO hotspot (name, latitude, longitude, radius, weight, status, location) VALUES
                    ('地铁站出口', 30.7562, 103.9342, 200, 80, 'ACTIVE', '犀浦地铁站'),
                    ('图书馆', 30.7528, 103.9310, 150, 60, 'ACTIVE', '西南交通大学图书馆'),
                    ('食堂', 30.7515, 103.9305, 150, 55, 'ACTIVE', '犀浦校区学生食堂'),
                    ('教学楼', 30.7532, 103.9318, 150, 50, 'ACTIVE', '犀浦校区教学楼'),
                    ('宿舍区', 30.7545, 103.9300, 200, 45, 'ACTIVE', '犀浦校区宿舍区'),
                    ('体育馆', 30.7505, 103.9290, 150, 40, 'ACTIVE', '犀浦校区体育馆'),
                    ('商业街', 30.7555, 103.9330, 150, 35, 'ACTIVE', '犀浦校区商业街'),
                    ('东门', 30.7542, 103.9320, 150, 30, 'ACTIVE', '犀浦校区东门'),
                    ('南门', 30.7525, 103.9300, 150, 25, 'ACTIVE', '犀浦校区南门'),
                    ('北门', 30.7498, 103.9275, 150, 20, 'ACTIVE', '犀浦校区北门')
                """);
                logger.info(" 热门区域数据插入成功");
            } else {
                logger.info(" 热门区域数据已存在，跳过插入");
            }
        } catch (Exception e) {
            logger.warn("插入热门区域数据时出错: {}", e.getMessage());
        }
    }

    private void updateScooterBindings() {
        try {
            jdbcTemplate.execute("UPDATE scooters SET depot_id = 1 WHERE id <= 5");
            jdbcTemplate.execute("UPDATE scooters SET depot_id = 2 WHERE id > 5 AND id <= 10");
            jdbcTemplate.execute("UPDATE scooters SET depot_id = 3 WHERE id > 10 AND id <= 15");
            jdbcTemplate.execute("UPDATE scooters SET depot_id = 4 WHERE id > 15 AND id <= 20");
            jdbcTemplate.execute("UPDATE scooters SET depot_id = 5 WHERE id > 20");
            logger.info(" 滑板车与服务点绑定完成");
        } catch (Exception e) {
            logger.warn("更新滑板车绑定时出错: {}", e.getMessage());
        }
    }
}
