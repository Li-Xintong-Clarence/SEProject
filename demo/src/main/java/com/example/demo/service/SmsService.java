package com.example.demo.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 短信验证码服务（模拟实现）
 * 用于演示目的，实际项目中应集成真实的短信服务商
 */
public interface SmsService {

    /**
     * 发送验证码
     * @param phone 手机号
     * @param type 验证码类型：LOGIN, REGISTER, PASSWORD_RESET, BIND_PHONE
     * @return 发送结果
     */
    Map<String, Object> sendVerificationCode(String phone, String type);

    /**
     * 验证验证码
     * @param phone 手机号
     * @param code 验证码
     * @param type 验证码类型
     * @return 验证结果
     */
    boolean verifyCode(String phone, String code, String type);

    /**
     * 检查验证码是否过期
     * @param phone 手机号
     * @return true=已过期或不存在，false=有效
     */
    boolean isCodeExpired(String phone, String type);
}
