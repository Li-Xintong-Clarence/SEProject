package com.example.demo.service.service;

import com.example.demo.service.service.impl.PaymentServiceImpl;
import com.example.demo.service.vo.PaymentRequest;
import com.example.demo.service.vo.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentServiceTest {

    // 因为不需要模拟数据库，我们直接实例化这个服务
    private PaymentServiceImpl paymentService;
    private PaymentRequest request;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl();
        request = new PaymentRequest();
        request.setBookingId(100L);
    }

    @Test
    void testProcessPayment_Success() {
        // 测试场景 1：模拟支付成功（合法的卡号和金额）
        request.setCardNumber("1234567890123456"); // 16位卡号
        request.setAmount(50.0);

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(50.0, response.getAmount());
        assertNotNull(response.getTransactionId());
        assertTrue(response.getTransactionId().startsWith("TXN-"));
    }

    @Test
    void testProcessPayment_InvalidCardNumber() {
        // 测试场景 2：模拟支付失败（卡号不足13位）
        request.setCardNumber("12345678");
        request.setAmount(50.0);

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals("FAILED", response.getStatus());
        assertEquals("Invalid card number", response.getMessage());
    }

    @Test
    void testProcessPayment_NullCardNumber() {
        // 测试场景 3：模拟支付失败（卡号为空）
        request.setCardNumber(null);
        request.setAmount(50.0);

        PaymentResponse response = paymentService.processPayment(request);

        assertEquals("FAILED", response.getStatus());
    }

    @Test
    void testProcessPayment_InvalidAmount() {
        // 测试场景 4：模拟支付失败（金额为负数或0）
        request.setCardNumber("1234567890123456");
        request.setAmount(-10.0); // 非法金额

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals("FAILED", response.getStatus());
        assertEquals("Invalid amount", response.getMessage());
    }
}