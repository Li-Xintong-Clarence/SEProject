package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 支付控制器
 * 处理支付相关的API请求
 * 路径: /api/payments/*
 */
@RestController
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    /**
     * 验证卡号格式
     * POST /api/payments/validate/card
     */
    @PostMapping("/validate/card")
    public Result<Map<String, String>> validateCard(@RequestBody Map<String, String> params) {
        String cardNumber = params.get("cardNumber");
        if (cardNumber == null || cardNumber.isEmpty()) {
            return Result.error("卡号不能为空");
        }
        boolean valid = paymentService.validateCardNumber(cardNumber);
        if (valid) {
            String cardType = paymentService.getCardType(cardNumber);
            return Result.success(Map.of("valid", "true", "cardType", cardType));
        }
        return Result.error("卡号格式无效");
    }

    /**
     * 验证CVV
     * POST /api/payments/validate/cvv
     */
    @PostMapping("/validate/cvv")
    public Result<Boolean> validateCVV(@RequestBody Map<String, String> params) {
        String cvv = params.get("cvv");
        String cardType = params.get("cardType");
        if (cvv == null || cvv.isEmpty()) {
            return Result.error("CVV不能为空");
        }
        boolean valid = paymentService.validateCVV(cvv, cardType);
        if (valid) {
            return Result.success(true);
        }
        return Result.error("CVV格式无效");
    }

    /**
     * 验证卡有效期
     * POST /api/payments/validate/expiry
     */
    @PostMapping("/validate/expiry")
    public Result<Boolean> validateExpiry(@RequestBody Map<String, String> params) {
        String expiry = params.get("expiry");
        if (expiry == null || expiry.isEmpty()) {
            return Result.error("有效期不能为空");
        }
        boolean valid = paymentService.validateExpiry(expiry);
        if (valid) {
            return Result.success(true);
        }
        return Result.error("有效期无效或已过期");
    }

    /**
     * 绑定银行卡
     * POST /api/payments/bind
     */
    @PostMapping("/bind")
    public Result<Map<String, String>> bindCard(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String cardNumber = params.get("cardNumber");
        String expiry = params.get("expiry");
        String cvv = params.get("cvv");

        if (cardNumber == null || expiry == null || cvv == null) {
            return Result.error("请提供完整的银行卡信息");
        }

        boolean success = paymentService.bindCard(userId, cardNumber, expiry, cvv);
        if (success) {
            String cardType = paymentService.getCardType(cardNumber);
            String last4 = cardNumber.substring(cardNumber.length() - 4);
            return Result.success(Map.of("cardLast4", last4, "cardType", cardType));
        }
        return Result.error("银行卡绑定失败，请检查卡号信息");
    }

    /**
     * 解除银行卡绑定
     * POST /api/payments/unbind
     */
    @PostMapping("/unbind")
    public Result<String> unbindCard(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean success = paymentService.unbindCard(userId);
        if (success) {
            return Result.success("银行卡解除绑定成功");
        }
        return Result.error("解除绑定失败");
    }

    /**
     * 获取绑定的银行卡信息
     * GET /api/payments/card
     */
    @GetMapping("/card")
    public Result<Map<String, Object>> getCardInfo(HttpServletRequest request) {
        // 这个功能需要用户服务支持，暂时返回模拟数据
        return Result.success(Map.of(
            "hasCard", false,
            "cardLast4", "",
            "cardType", ""
        ));
    }

    /**
     * 设置支付密码
     * POST /api/payments/password/set
     */
    @PostMapping("/password/set")
    public Result<String> setPaymentPassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String password = params.get("password");

        if (password == null || password.isEmpty()) {
            return Result.error("支付密码不能为空");
        }

        boolean success = paymentService.setPaymentPassword(userId, password);
        if (success) {
            return Result.success("支付密码设置成功");
        }
        return Result.error("支付密码设置失败，密码必须为6位数字");
    }

    /**
     * 验证支付密码
     * POST /api/payments/password/verify
     */
    @PostMapping("/password/verify")
    public Result<Boolean> verifyPaymentPassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String password = params.get("password");

        if (password == null || password.isEmpty()) {
            return Result.error("支付密码不能为空");
        }

        boolean valid = paymentService.verifyPaymentPassword(userId, password);
        if (valid) {
            return Result.success(true);
        }
        return Result.error("支付密码错误");
    }

    /**
     * Token支付（使用已绑定的银行卡）
     * POST /api/payments/pay
     */
    @PostMapping("/pay")
    public Result<String> payWithToken(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Double amount = params.get("amount") != null ? Double.parseDouble(params.get("amount").toString()) : 0;
        String paymentPassword = params.get("paymentPassword") != null ? params.get("paymentPassword").toString() : null;

        if (amount <= 0) {
            return Result.error("支付金额必须大于0");
        }

        boolean success = paymentService.payWithToken(userId, amount, paymentPassword);
        if (success) {
            return Result.success("支付成功");
        }
        return Result.error("支付失败，请检查支付密码或银行卡信息");
    }

    /**
     * 生成卡号Token（用于前端提交）
     * POST /api/payments/token
     */
    @PostMapping("/token")
    public Result<String> generateToken(@RequestBody Map<String, String> params) {
        String cardNumber = params.get("cardNumber");
        if (cardNumber == null || cardNumber.isEmpty()) {
            return Result.error("卡号不能为空");
        }

        if (!paymentService.validateCardNumber(cardNumber)) {
            return Result.error("卡号格式无效");
        }

        String token = paymentService.generateCardToken(cardNumber);
        if (token != null) {
            return Result.success(token);
        }
        return Result.error("Token生成失败");
    }

    /**
     * 掩码卡号（用于日志）
     * POST /api/payments/mask
     */
    @PostMapping("/mask")
    public Result<String> maskCard(@RequestBody Map<String, String> params) {
        String cardNumber = params.get("cardNumber");
        if (cardNumber == null || cardNumber.isEmpty()) {
            return Result.error("卡号不能为空");
        }
        String masked = paymentService.maskCardNumber(cardNumber);
        return Result.success(masked);
    }
}
