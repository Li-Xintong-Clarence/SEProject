package com.example.demo.service.impl;

import com.example.demo.service.PaymentService;
import com.example.demo.vo.PaymentRequest;
import com.example.demo.vo.PaymentResponse;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 支付服务实现类
 * 实现支付相关的业务逻辑（ID6：处理卡片支付 - 模拟支付）
 * 该服务模拟支付处理，不进行真实的银行卡集成
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    /**
     * 处理支付请求
     * 1. 验证卡号（模拟：13位以上）
     * 2. 验证金额（大于0）
     * 3. 生成支付响应（模拟成功）
     */
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        PaymentResponse response = new PaymentResponse();

        // Basic validation (simulated)
        if (request.getCardNumber() == null || request.getCardNumber().length() < 13) {
            response.setStatus("FAILED");
            response.setMessage("Invalid card number");
            response.setPaymentTime(LocalDateTime.now());
            return response;
        }

        if (request.getAmount() == null || request.getAmount() <= 0) {
            response.setStatus("FAILED");
            response.setMessage("Invalid amount");
            response.setPaymentTime(LocalDateTime.now());
            return response;
        }

        // Simulate successful payment
        response.setPaymentId(System.currentTimeMillis());
        response.setBookingId(request.getBookingId());
        response.setStatus("SUCCESS");
        response.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        response.setAmount(request.getAmount());
        response.setPaymentTime(LocalDateTime.now());
        response.setMessage("Payment successful (simulated)");

        return response;
    }
}
