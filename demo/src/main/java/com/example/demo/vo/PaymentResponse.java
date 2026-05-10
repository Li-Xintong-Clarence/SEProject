package com.example.demo.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 支付响应类
 * 用于返回支付结果的响应数据（ID6：处理卡片支付 - 模拟支付）
 */
@Data
public class PaymentResponse {
    private Long paymentId;           // 支付ID
    private Long bookingId;           // 预订ID
    private String status;           // 支付状态：SUCCESS（成功）, FAILED（失败）
    private String transactionId;    // 交易ID（模拟）
    private Double amount;           // 支付金额
    private LocalDateTime paymentTime;  // 支付时间
    private String message;          // 支付结果消息
}
