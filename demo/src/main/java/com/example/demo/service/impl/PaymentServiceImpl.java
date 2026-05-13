package com.example.demo.service.impl;

import com.example.demo.mapper.UserMapper;
import com.example.demo.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 支付服务实现类
 * 实现支付安全功能：
 * 1. 支付密码验证（BCrypt加密）
 * 2. 卡号Token化（模拟PCI-DSS标准）
 * 3. 兼容旧接口以支持平滑迁移
 *
 * 未来可扩展集成：
 * - Stripe Payment Intents API
 * - 微信支付
 * - 支付宝
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecureRandom secureRandom = new SecureRandom();

    // Token缓存（生产环境应使用Redis）
    private final Map<String, String> tokenStore = new HashMap<>();

    // ============ 支付密码相关 ============

    @Override
    public boolean verifyPaymentPassword(Long userId, String paymentPassword) {
        if (userId == null || paymentPassword == null || paymentPassword.isEmpty()) {
            logger.warn("支付密码验证失败：参数为空");
            return false;
        }

        var user = userMapper.findById(userId);
        if (user == null) {
            logger.warn("支付密码验证失败：用户不存在, userId={}", userId);
            return false;
        }

        if (user.getPaymentPassword() == null || user.getPaymentPassword().isEmpty()) {
            logger.warn("支付密码验证失败：用户未设置支付密码, userId={}", userId);
            return false;
        }

        boolean matches = passwordEncoder.matches(paymentPassword, user.getPaymentPassword());
        if (matches) {
            logger.info("支付密码验证成功, userId={}", userId);
        } else {
            logger.warn("支付密码验证失败：密码不匹配, userId={}", userId);
        }
        return matches;
    }

    @Override
    public boolean setPaymentPassword(Long userId, String paymentPassword) {
        if (userId == null || paymentPassword == null) {
            logger.warn("设置支付密码失败：参数为空");
            return false;
        }

        // 验证支付密码强度
        if (!isValidPaymentPassword(paymentPassword)) {
            logger.warn("设置支付密码失败：密码强度不足");
            return false;
        }

        var user = userMapper.findById(userId);
        if (user == null) {
            logger.warn("设置支付密码失败：用户不存在, userId={}", userId);
            return false;
        }

        // BCrypt加密存储
        String encodedPassword = passwordEncoder.encode(paymentPassword);
        user.setPaymentPassword(encodedPassword);
        userMapper.update(user);

        logger.info("支付密码设置成功, userId={}", userId);
        return true;
    }

    /**
     * 验证支付密码强度
     * 要求：6位数字
     */
    private boolean isValidPaymentPassword(String password) {
        if (password == null || password.length() != 6) {
            return false;
        }
        return password.matches("\\d{6}");
    }

    // ============ 卡号Token化相关 ============

    @Override
    public String generateCardToken(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return null;
        }

        // 生成安全的随机Token
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        // 使用Token存储真实卡号（生产环境应加密存储）
        tokenStore.put(token, cardNumber);

        logger.info("生成卡号Token成功, token={}", token.substring(0, 10) + "...");
        return token;
    }

    /**
     * 根据Token获取真实卡号（仅内部使用）
     */
    public String getCardNumberByToken(String token) {
        return tokenStore.get(token);
    }

    @Override
    public boolean bindCard(Long userId, String cardNumber, String expiryDate, String cvv) {
        if (userId == null) {
            logger.warn("绑定银行卡失败：用户ID为空");
            return false;
        }

        // 验证卡号格式
        if (!validateCardNumber(cardNumber)) {
            logger.warn("绑定银行卡失败：卡号格式无效");
            return false;
        }

        // 验证CVV
        String cardType = getCardType(cardNumber);
        if (!validateCVV(cvv, cardType)) {
            logger.warn("绑定银行卡失败：CVV无效");
            return false;
        }

        // 验证有效期
        if (!validateExpiry(expiryDate)) {
            logger.warn("绑定银行卡失败：有效期无效或已过期");
            return false;
        }

        var user = userMapper.findById(userId);
        if (user == null) {
            logger.warn("绑定银行卡失败：用户不存在, userId={}", userId);
            return false;
        }

        // 生成Token并存储
        String cardToken = generateCardToken(cardNumber);
        String cardLast4 = cardNumber.substring(cardNumber.length() - 4);

        user.setCardToken(cardToken);
        user.setCardLast4(cardLast4);
        user.setCardType(cardType);
        user.setHasCard(true);
        userMapper.update(user);

        logger.info("银行卡绑定成功, userId={}, cardLast4={}, cardType={}", userId, cardLast4, cardType);
        return true;
    }

    @Override
    public boolean unbindCard(Long userId) {
        if (userId == null) {
            return false;
        }

        var user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }

        // 清除Token
        if (user.getCardToken() != null) {
            tokenStore.remove(user.getCardToken());
        }

        user.setCardToken(null);
        user.setCardLast4(null);
        user.setCardType(null);
        user.setHasCard(false);
        userMapper.update(user);

        logger.info("银行卡解除绑定成功, userId={}", userId);
        return true;
    }

    @Override
    public boolean payWithToken(Long userId, double amount, String paymentPassword) {
        if (userId == null || amount <= 0) {
            logger.warn("Token支付失败：参数无效");
            return false;
        }

        var user = userMapper.findById(userId);
        if (user == null || !user.getHasCard()) {
            logger.warn("Token支付失败：用户未绑定银行卡, userId={}", userId);
            return false;
        }

        // 验证支付密码（如果用户设置了的话）
        if (user.getPaymentPassword() != null && !user.getPaymentPassword().isEmpty()) {
            if (paymentPassword == null || paymentPassword.isEmpty()) {
                logger.warn("Token支付失败：需要支付密码");
                return false;
            }
            if (!verifyPaymentPassword(userId, paymentPassword)) {
                logger.warn("Token支付失败：支付密码错误");
                return false;
            }
        }

        // 模拟支付处理（未来可替换为Stripe等第三方支付）
        logger.info("Token支付成功, userId={}, amount={}, cardType={}", userId, amount, user.getCardType());
        return true;
    }

    @Override
    public boolean processPayment(Long userId, String cardLast4, double amount, String paymentMethod) {
        if (userId == null || amount <= 0) {
            logger.warn("支付处理失败：参数无效");
            return false;
        }

        // 记录支付日志（生产环境应记录到数据库）
        logger.info("支付处理, userId={}, cardLast4={}, amount={}, method={}",
                userId, cardLast4 != null ? "****" : "N/A", amount, paymentMethod);

        // 模拟支付成功
        return true;
    }

    // ============ 卡号验证相关 ============

    @Override
    public boolean validateCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return false;
        }

        // 移除空格和连字符
        String cleaned = cardNumber.replaceAll("[\\s-]", "");

        // 检查是否为数字
        if (!cleaned.matches("\\d+")) {
            return false;
        }

        // 检查长度（13-19位）
        if (cleaned.length() < 13 || cleaned.length() > 19) {
            return false;
        }

        // Luhn算法验证
        return luhnCheck(cleaned);
    }

    /**
     * Luhn算法（用于验证信用卡号码）
     */
    private boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return sum % 10 == 0;
    }

    @Override
    public String getCardType(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return "UNKNOWN";
        }

        String cleaned = cardNumber.replaceAll("[\\s-]", "");

        // VISA: 以4开头，13或16位
        if (cleaned.startsWith("4") && (cleaned.length() == 13 || cleaned.length() == 16)) {
            return "VISA";
        }

        // MasterCard: 以51-55或2221-2720开头，16位
        if ((cleaned.length() == 16) && (
            (cleaned.startsWith("51") || cleaned.startsWith("52") ||
             cleaned.startsWith("53") || cleaned.startsWith("54") ||
             cleaned.startsWith("55")) ||
            (cleaned.startsWith("2221") || cleaned.startsWith("2720"))
        )) {
            return "MASTERCARD";
        }

        // American Express: 以34或37开头，15位
        if (cleaned.startsWith("34") || cleaned.startsWith("37")) {
            return "AMEX";
        }

        // Discover: 以6011、65、644-649开头，16位
        if ((cleaned.length() == 16) && (
            cleaned.startsWith("6011") || cleaned.startsWith("65") ||
            (cleaned.startsWith("644") || cleaned.startsWith("645") ||
             cleaned.startsWith("646") || cleaned.startsWith("647") ||
             cleaned.startsWith("648") || cleaned.startsWith("649"))
        )) {
            return "DISCOVER";
        }

        return "UNKNOWN";
    }

    @Override
    public boolean validateCVV(String cvv, String cardType) {
        if (cvv == null || cvv.isEmpty()) {
            return false;
        }

        // American Express使用4位CVV，其他使用3位
        if ("AMEX".equals(cardType)) {
            return cvv.matches("\\d{4}");
        }
        return cvv.matches("\\d{3}");
    }

    @Override
    public boolean validateExpiry(String expiryDate) {
        if (expiryDate == null || expiryDate.isEmpty()) {
            return false;
        }

        // 格式验证 MM/YY
        if (!expiryDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            return false;
        }

        // 解析日期
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt("20" + parts[1]);

        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        // 检查是否过期
        if (year < currentYear) {
            return false;
        }
        if (year == currentYear && month < currentMonth) {
            return false;
        }

        // 检查是否在合理范围内（未来10年内）
        if (year > currentYear + 10) {
            return false;
        }

        return true;
    }

    @Override
    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }

        String cleaned = cardNumber.replaceAll("[\\s-]", "");
        String last4 = cleaned.substring(cleaned.length() - 4);

        return "**** **** **** " + last4;
    }
}
