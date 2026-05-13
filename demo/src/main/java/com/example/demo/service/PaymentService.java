package com.example.demo.service;

/**
 * 支付服务接口
 * 定义支付相关的核心业务操作
 * 支持模拟支付和未来集成第三方支付（Stripe/微信/支付宝）
 */
public interface PaymentService {

    /**
     * 验证支付密码
     * @param userId 用户ID
     * @param paymentPassword 支付密码（明文）
     * @return 验证是否成功
     */
    boolean verifyPaymentPassword(Long userId, String paymentPassword);

    /**
     * 设置支付密码
     * @param userId 用户ID
     * @param paymentPassword 支付密码（明文，将加密存储）
     * @return 是否设置成功
     */
    boolean setPaymentPassword(Long userId, String paymentPassword);

    /**
     * 生成卡号Token（Tokenization）
     * 将真实卡号转换为安全的Token存储
     * @param cardNumber 完整卡号
     * @return 卡号Token
     */
    String generateCardToken(String cardNumber);

    /**
     * 绑定银行卡
     * @param userId 用户ID
     * @param cardNumber 完整卡号
     * @param expiryDate 有效期（MM/YY格式）
     * @param cvv CVV码
     * @return 绑定是否成功
     */
    boolean bindCard(Long userId, String cardNumber, String expiryDate, String cvv);

    /**
     * 解除银行卡绑定
     * @param userId 用户ID
     * @return 解除绑定是否成功
     */
    boolean unbindCard(Long userId);

    /**
     * 使用Token支付
     * @param userId 用户ID
     * @param amount 支付金额
     * @param paymentPassword 支付密码（可选，用于高风险操作）
     * @return 支付是否成功
     */
    boolean payWithToken(Long userId, double amount, String paymentPassword);

    /**
     * 处理支付（兼容旧接口）
     * @param userId 用户ID
     * @param cardLast4 卡号后4位
     * @param amount 支付金额
     * @param paymentMethod 支付方式
     * @return 支付是否成功
     */
    boolean processPayment(Long userId, String cardLast4, double amount, String paymentMethod);

    /**
     * 验证卡号格式（Luhn算法）
     * @param cardNumber 卡号
     * @return 是否有效
     */
    boolean validateCardNumber(String cardNumber);

    /**
     * 获取卡类型（VISA, MASTERCARD, AMEX等）
     * @param cardNumber 卡号
     * @return 卡类型
     */
    String getCardType(String cardNumber);

    /**
     * 验证CVV格式
     * @param cvv CVV码
     * @param cardType 卡类型
     * @return 是否有效
     */
    boolean validateCVV(String cvv, String cardType);

    /**
     * 验证卡有效期
     * @param expiryDate 有效期（MM/YY格式）
     * @return 是否有效
     */
    boolean validateExpiry(String expiryDate);

    /**
     * 掩码卡号（用于日志和显示）
     * @param cardNumber 完整卡号
     * @return 掩码后的卡号，如 **** **** **** 1234
     */
    String maskCardNumber(String cardNumber);
}
