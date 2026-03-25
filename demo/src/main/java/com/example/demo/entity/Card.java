package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 支付卡实体类
 * 对应数据库card表
 * 存储用户的支付卡信息
 */
@Data
public class Card {
    /** 支付卡ID（主键） */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 卡号（生产环境应加密存储） */
    private String cardNumber;
    /** 持卡人姓名 */
    private String cardHolder;
    /** 有效期（格式：MM/YYYY） */
    private String expiryDate;
    /** CVV安全码（生产环境不应存储或应加密） */
    private String cvv;
    /** 是否为默认支付卡 */
    private Boolean isDefault;
    /** 创建时间 */
    private LocalDateTime createdAt;
}
