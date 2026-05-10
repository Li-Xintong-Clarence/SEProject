package com.example.demo.service;

import com.example.demo.vo.PaymentRequest;
import com.example.demo.vo.PaymentResponse;

/**
 * 支付服务接口
 * 定义支付相关的业务逻辑方法（ID6：处理卡片支付 - 模拟支付）
 */
public interface PaymentService {
    /**
     * 处理支付请求
     * @param request 支付请求
     * @return 支付响应结果
     */
    PaymentResponse processPayment(PaymentRequest request);
}
