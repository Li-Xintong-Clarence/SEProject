package com.example.demo.vo;

import lombok.Data;

/**
 * 支付请求类
 * 用于处理支付的请求参数（ID6：处理卡片支付 - 模拟支付）
 */
@Data
public class PaymentRequest {
    private Long bookingId;              // 预订ID
    private String cardNumber;          // 卡号（模拟：16位数字）
    private String cardHolderName;      // 持卡人姓名
    private String expiryDate;         // 有效期（MM/YY格式）
    private String cvv;                // CVV验证码（3-4位）
    private Double amount;              // 支付金额
}
