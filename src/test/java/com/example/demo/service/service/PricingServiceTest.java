package com.example.demo.service;

import com.example.demo.service.entity.Pricing;
import com.example.demo.service.mapper.PricingMapper;
import com.example.demo.service.service.impl.PricingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PricingServiceTest {

    @Mock
    private PricingMapper pricingMapper;

    @InjectMocks
    private PricingServiceImpl pricingService;

    private Pricing dummyPricing;

    @BeforeEach
    void setUp() {
        // 初始化测试用的价格数据
        dummyPricing = new Pricing();
        dummyPricing.setId(1L);
        // 如果你的 Pricing 实体类中没有 hireOption 字段，请将下面这行删掉或注释掉
        // dummyPricing.setHireOption("1hr");
        dummyPricing.setPrice(new BigDecimal("5.00"));
    }

    @Test
    void testFindAll_Success() {
        // 模拟数据库返回一个列表
        when(pricingMapper.findAll()).thenReturn(Arrays.asList(dummyPricing));

        // 执行查询
        List<Pricing> result = pricingService.findAll();

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(pricingMapper, times(1)).findAll();
    }

    @Test
    void testFindById_Success() {
        // 模拟数据库按ID查询
        when(pricingMapper.findById(1L)).thenReturn(dummyPricing);

        Pricing result = pricingService.findById(1L);

        assertNotNull(result);
        assertEquals(new BigDecimal("5.00"), result.getPrice());
        verify(pricingMapper, times(1)).findById(1L);
    }

    @Test
    void testSave_Success() {
        // 模拟插入成功返回受影响的行数 1
        when(pricingMapper.insert(any(Pricing.class))).thenReturn(1);

        boolean result = pricingService.save(dummyPricing);

        assertTrue(result);
        verify(pricingMapper, times(1)).insert(dummyPricing);
    }

    @Test
    void testUpdate_Success() {
        // 模拟更新成功返回受影响的行数 1
        when(pricingMapper.update(any(Pricing.class))).thenReturn(1);

        boolean result = pricingService.update(dummyPricing);

        assertTrue(result);
        verify(pricingMapper, times(1)).update(dummyPricing);
    }

    @Test
    void testDeleteById_Success() {
        // 模拟删除成功返回受影响的行数 1
        when(pricingMapper.deleteById(1L)).thenReturn(1);

        boolean result = pricingService.deleteById(1L);

        assertTrue(result);
        verify(pricingMapper, times(1)).deleteById(1L);
    }
}