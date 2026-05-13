package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 支付卡实体类
 *
 * 【安全设计说明 - PCI DSS 最小化原则】
 *
 * 为符合支付卡行业数据安全标准 (PCI DSS)，本实体采用最小化存储策略：
 *
 * ✅ 安全实践：
 * - 只存储后4位卡号（用于用户识别）
 * - 不存储完整卡号（无法被恶意利用）
 * - 不存储 CVV（支付时实时验证）
 * - 不存储敏感数据
 *
 * ⚠️ 生产环境要求：
 * - 完整卡号应通过第三方支付平台（如 Stripe、微信支付）处理
 * - 使用 Tokenization 代替直接存储卡号
 * - 所有传输必须通过 HTTPS
 * - 卡号验证应使用 Luhn 算法
 *
 * 更多信息请参考：https://www.pcisecuritystandards.org/
 */
@Data
public class Card {
    /** 支付卡ID（主键） */
    private Long id;

    /** 用户ID */
    private Long userId;

    /**
     * 持卡人姓名
     * 非敏感信息，可正常存储
     */
    private String cardHolder;

    /**
     * 卡号后4位
     * 仅存储最后4位用于识别，用户无法通过此信息进行支付
     */
    private String lastFour;

    /**
     * 卡片类型
     * 如：VISA、Mastercard、UnionPay 等
     */
    private String cardType;

    /**
     * 有效期（格式：MM/YYYY）
     * 用于显示和基本验证，不含 CVV
     */
    private String expiryDate;

    /**
     * 是否为默认支付卡
     */
    private Boolean isDefault;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /**
     * 获取脱敏卡号显示
     * 格式：**** **** **** 1234
     */
    public String getMaskedNumber() {
        if (lastFour == null || lastFour.length() != 4) {
            return "****";
        }
        return "**** **** **** " + lastFour;
    }
}
