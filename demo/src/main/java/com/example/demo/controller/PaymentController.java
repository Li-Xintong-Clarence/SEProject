package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.BookingService;
import com.example.demo.service.PaymentService;
import com.example.demo.vo.PaymentRequest;
import com.example.demo.vo.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 * 处理支付相关的API请求（ID6：处理预订的卡片支付 - 模拟支付）
 * 模拟支付处理，不进行真实的银行卡集成
 */
@RestController
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private BookingService bookingService;

    /**
     * 处理支付请求（ID6：处理预订的卡片支付 - 模拟支付）
     * 用户为预订的滑板车进行支付
     * @param request 支付请求，包含预订ID和支付金额
     * @return 支付成功返回支付响应，失败返回错误信息
     */
    @PostMapping
    public Result<PaymentResponse> pay(@RequestBody PaymentRequest request) {
        // Verify booking exists
        var booking = bookingService.findById(request.getBookingId());
        if (booking == null) {
            return Result.error("Booking not found");
        }

        // Set amount from booking if not provided
        if (request.getAmount() == null) {
            request.setAmount(booking.getCost().doubleValue());
        }

        PaymentResponse response = paymentService.processPayment(request);
        return Result.success(response);
    }
}
