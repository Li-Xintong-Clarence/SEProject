package com.example.demo.service;

import com.example.demo.entity.Booking;
import com.example.demo.entity.Pricing;
import com.example.demo.entity.User;
import com.example.demo.mapper.BookingMapper;
import com.example.demo.mapper.PricingMapper;
import com.example.demo.mapper.ScooterMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private PricingMapper pricingMapper;
    @Mock
    private ScooterMapper scooterMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ScooterService scooterService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking dummyBooking;
    private Pricing dummyPricing;
    private User dummyUser;

    @BeforeEach
    void setUp() {
        // 初始化测试用的虚拟数据
        dummyBooking = new Booking();
        dummyBooking.setId(1L);
        dummyBooking.setUserId(100L);
        dummyBooking.setScooterId(200L);
        dummyBooking.setHireOption("1hr");
        dummyBooking.setTotalCost(new BigDecimal("5.00"));

        dummyPricing = new Pricing();
        dummyPricing.setId(1L);
        dummyPricing.setPrice(new BigDecimal("5.00"));

        dummyUser = new User();
        dummyUser.setId(100L);
        dummyUser.setUsername("testUser");
        dummyUser.setEmail("test@example.com");
    }

    @Test
    void testSaveBooking_Success() {
        // 模拟数据库查询价格的行为
        when(pricingMapper.findById(1L)).thenReturn(dummyPricing);
        when(bookingMapper.insert(any(Booking.class))).thenReturn(1);

        // 执行保存
        boolean result = bookingService.save(dummyBooking);

        // 验证结果：断言保存成功，并且状态被正确设置为 PENDING
        assertTrue(result);
        assertEquals("PENDING", dummyBooking.getStatus());
        assertNotNull(dummyBooking.getConfirmationCode());
        verify(bookingMapper, times(1)).insert(dummyBooking);
    }

    @Test
    void testPayBooking_Success() {
        // 准备数据：订单状态必须是 PENDING
        dummyBooking.setStatus("PENDING");

        // 模拟 Mapper 和 Service 行为
        when(bookingMapper.findById(1L)).thenReturn(dummyBooking);
        when(userMapper.findById(anyLong())).thenReturn(dummyUser);
        when(bookingMapper.update(any(Booking.class))).thenReturn(1);

        // 执行支付逻辑
        boolean result = bookingService.payBooking(1L);

        // 验证结果：状态变为 PAID，并且调用了更新车辆状态和发邮件的方法
        assertTrue(result);
        assertEquals("PAID", dummyBooking.getStatus());
        assertNotNull(dummyBooking.getStartTime());
        assertNotNull(dummyBooking.getEndTime());

        verify(scooterService, times(1)).updateStatus(200L, "IN_USE");
        verify(emailService, times(1)).sendBookingConfirmation(
                anyString(), anyString(), any(), any(), anyString(), anyString(), anyString(), anyDouble()
        );
    }

    @Test
    void testExtendBooking_Success() {
        // 准备数据：订单必须是 ACTIVE
        dummyBooking.setStatus("ACTIVE");
        dummyBooking.setEndTime(LocalDateTime.now().plusHours(1));

        when(bookingMapper.findById(1L)).thenReturn(dummyBooking);
        // "4hr" 对应的 pricingId 是 2L
        Pricing extendPricing = new Pricing();
        extendPricing.setPrice(new BigDecimal("15.00"));
        when(pricingMapper.findById(2L)).thenReturn(extendPricing);
        when(bookingMapper.update(any(Booking.class))).thenReturn(1);

        // 执行延期逻辑
        boolean result = bookingService.extendBooking(1L, "4hr");

        // 验证：总价应该变为 5.00 + 15.00 = 20.00
        assertTrue(result);
        assertEquals(new BigDecimal("20.00"), dummyBooking.getTotalCost());
        verify(bookingMapper, times(1)).update(dummyBooking);
    }

    @Test
    void testCancelBooking_Success() {
        // 准备数据：订单为 PENDING 或 ACTIVE
        dummyBooking.setStatus("PENDING");

        when(bookingMapper.findById(1L)).thenReturn(dummyBooking);
        when(bookingMapper.update(any(Booking.class))).thenReturn(1);

        // 执行取消
        boolean result = bookingService.cancelBooking(1L);

        // 验证：状态变为 CANCELLED，车辆释放
        assertTrue(result);
        assertEquals("CANCELLED", dummyBooking.getStatus());
        verify(scooterService, times(1)).updateStatus(200L, "AVAILABLE");
    }
}