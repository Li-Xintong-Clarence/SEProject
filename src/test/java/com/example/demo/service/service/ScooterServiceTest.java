package com.example.demo.service.service;

import com.example.demo.service.entity.Scooter;
import com.example.demo.service.mapper.ScooterMapper;
import com.example.demo.service.service.impl.ScooterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal; // 引入了 BigDecimal
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScooterServiceTest {

    @Mock
    private ScooterMapper scooterMapper;

    @InjectMocks
    private ScooterServiceImpl scooterService;

    private Scooter dummyScooter;

    @BeforeEach
    void setUp() {
        // 初始化一辆测试用的滑板车
        dummyScooter = new Scooter();
        dummyScooter.setId(200L);
        dummyScooter.setScooterNumber("SCOOT-001");
        dummyScooter.setStatus("AVAILABLE");
        // 修复点：将普通的 95 转换为 BigDecimal 类型
        dummyScooter.setBatteryLevel(new BigDecimal("95"));
        dummyScooter.setLocation("Central Park");
    }

    @Test
    void testFindAvailable_ReturnsOnlyAvailableScooters() {
        // 模拟数据库只返回状态为 AVAILABLE 的车辆
        when(scooterMapper.findAvailable()).thenReturn(Arrays.asList(dummyScooter));

        // 执行查询可用车辆的方法
        List<Scooter> availableScooters = scooterService.findAvailable();

        // 验证结果
        assertNotNull(availableScooters);
        assertEquals(1, availableScooters.size());
        assertEquals("AVAILABLE", availableScooters.get(0).getStatus());
        verify(scooterMapper, times(1)).findAvailable();
    }

    @Test
    void testUpdateStatus_Success() {
        // 模拟状态更新成功（受影响行数为1）
        when(scooterMapper.updateStatus(200L, "IN_USE")).thenReturn(1);

        // 执行状态更新：将车辆状态改为使用中
        boolean result = scooterService.updateStatus(200L, "IN_USE");

        // 验证更新成功，并且 Mapper 确实被调用了带有正确参数的方法
        assertTrue(result);
        verify(scooterMapper, times(1)).updateStatus(200L, "IN_USE");
    }

    @Test
    void testSave_Success() {
        // 模拟新增滑板车成功
        when(scooterMapper.insert(any(Scooter.class))).thenReturn(1);

        boolean result = scooterService.save(dummyScooter);

        assertTrue(result);
        verify(scooterMapper, times(1)).insert(dummyScooter);
    }

    @Test
    void testFindById_Success() {
        // 模拟按ID查找滑板车
        when(scooterMapper.findById(200L)).thenReturn(dummyScooter);

        Scooter result = scooterService.findById(200L);

        assertNotNull(result);
        assertEquals("SCOOT-001", result.getScooterNumber());
        // 修复点：在断言中也使用 BigDecimal 进行比较
        assertEquals(new BigDecimal("95"), result.getBatteryLevel());
        verify(scooterMapper, times(1)).findById(200L);
    }
}