package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 短信验证码控制器
 * 提供验证码发送和验证接口
 */
@RestController
@RequestMapping("/api/sms")
@CrossOrigin
public class SmsController {

    @Autowired
    private SmsService smsService;

    /**
     * 发送验证码
     * POST /api/sms/send
     */
    @PostMapping("/send")
    public Result<Map<String, Object>> sendCode(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String type = params.getOrDefault("type", "LOGIN");

        if (phone == null || phone.isEmpty()) {
            return Result.error("手机号不能为空");
        }

        Map<String, Object> result = smsService.sendVerificationCode(phone, type);
        boolean success = (boolean) result.get("success");

        if (success) {
            return Result.success(result);
        } else {
            return Result.error((String) result.get("message"));
        }
    }

    /**
     * 验证验证码
     * POST /api/sms/verify
     */
    @PostMapping("/verify")
    public Result<Map<String, Object>> verifyCode(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String code = params.get("code");
        String type = params.getOrDefault("type", "LOGIN");

        if (phone == null || phone.isEmpty()) {
            return Result.error("手机号不能为空");
        }
        if (code == null || code.isEmpty()) {
            return Result.error("验证码不能为空");
        }

        boolean valid = smsService.verifyCode(phone, code, type);

        if (valid) {
            return Result.success(Map.of("valid", true, "message", "验证成功"));
        } else {
            return Result.error("验证码错误或已过期");
        }
    }
}
