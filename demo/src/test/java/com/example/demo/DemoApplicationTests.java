package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 启动测试类
 * 测试Spring Boot应用能否正常加载上下文
 */
@SpringBootTest
class DemoApplicationTests {

    /**
     * 测试应用上下文能否正常加载
     * 如果没有报错，说明项目配置正确（数据库、MyBatis等都能正常启动）
     */
    @Test
    void contextLoads() {
    }

}
