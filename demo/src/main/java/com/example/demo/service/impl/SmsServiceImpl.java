package com.example.demo.service.impl;

import com.example.demo.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 短信验证码服务实现（模拟）
 * 生成6位数字验证码，有效期5分钟
 */
@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    // 存储验证码：key = phone:type, value = [code, timestamp]
    private final Map<String, long[]> verificationCodes = new ConcurrentHashMap<>();

    // 验证码有效期：5分钟
    private static final long CODE_EXPIRY_MS = 5 * 60 * 1000;

    // 发送间隔：60秒
    private static final long SEND_INTERVAL_MS = 60 * 1000;

    // 存储发送间隔：key = phone, value = lastSendTimestamp
    private final Map<String, Long> sendTimestamps = new ConcurrentHashMap<>();

    private final Random random = new Random();

    /**
     * 生成6位数字验证码
     */
    private String generateCode() {
        return String.format("%06d", random.nextInt(1000000));
    }

    /**
     * 发送验证码
     */
    @Override
    public Map<String, Object> sendVerificationCode(String phone, String type) {
        logger.info("Sending verification code to phone: {}, type: {}", phone, type);

        // 验证手机号格式
        if (!isValidPhone(phone)) {
            return Map.of(
                "success", false,
                "message", "手机号格式不正确"
            );
        }

        // 检查发送间隔
        Long lastSend = sendTimestamps.get(phone);
        if (lastSend != null && System.currentTimeMillis() - lastSend < SEND_INTERVAL_MS) {
            long remaining = (SEND_INTERVAL_MS - (System.currentTimeMillis() - lastSend)) / 1000;
            return Map.of(
                "success", false,
                "message", String.format("请%d秒后再试", remaining),
                "retryAfter", remaining
            );
        }

        // 生成验证码
        String code = generateCode();
        String key = phone + ":" + type;

        // 存储验证码和发送时间
        verificationCodes.put(key, new long[]{Long.parseLong(code), System.currentTimeMillis()});
        sendTimestamps.put(phone, System.currentTimeMillis());

        // 模拟发送成功（实际项目中这里会调用短信服务商API）
        logger.info("Verification code generated for {}: {} (模拟发送)", phone, code);

        // 返回成功信息（开发模式下返回验证码以便测试）
        return Map.of(
            "success", true,
            "message", "验证码已发送",
            // 开发环境下返回验证码，生产环境应移除
            "code", code,
            "expiresIn", 300
        );
    }

    /**
     * 验证验证码
     */
    @Override
    public boolean verifyCode(String phone, String code, String type) {
        String key = phone + ":" + type;
        long[] data = verificationCodes.get(key);

        if (data == null) {
            logger.warn("Verification code not found for: {}", key);
            return false;
        }

        // 检查是否过期
        if (isCodeExpired(phone, type)) {
            logger.warn("Verification code expired for: {}", key);
            verificationCodes.remove(key);
            return false;
        }

        // 验证验证码
        boolean valid = String.valueOf(data[0]).equals(code);

        if (valid) {
            // 验证成功后删除验证码（一次性使用）
            verificationCodes.remove(key);
            logger.info("Verification code verified successfully for: {}", phone);
        } else {
            logger.warn("Invalid verification code for: {}", key);
        }

        return valid;
    }

    /**
     * 检查验证码是否过期
     */
    @Override
    public boolean isCodeExpired(String phone, String type) {
        String key = phone + ":" + type;
        long[] data = verificationCodes.get(key);

        if (data == null) {
            return true;
        }

        return System.currentTimeMillis() - data[1] > CODE_EXPIRY_MS;
    }

    /**
     * 验证手机号格式（中国大陆手机号）
     */
    private boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        // 支持 +86 前缀或直接11位数字
        String cleanPhone = phone.replace("+86", "").trim();
        return cleanPhone.matches("^1[3-9]\\d{9}$");
    }

    /**
     * 清理过期验证码（可定时调用）
     */
    public void cleanupExpiredCodes() {
        long now = System.currentTimeMillis();
        verificationCodes.entrySet().removeIf(entry ->
            now - entry.getValue()[1] > CODE_EXPIRY_MS
        );
    }
}
