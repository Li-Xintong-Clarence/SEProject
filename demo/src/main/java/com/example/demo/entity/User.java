package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库users表
 */
@Data
public class User {
    /** 用户ID（主键） */
    private Long id;
    /** 用户名（唯一） */
    private String username;
    /** 密码（BCrypt加密存储） */
    private String password;
    /** 邮箱 */
    private String email;
    /** 手机号 */
    private String phone;
    /** 角色：CUSTOMER=普通用户，ADMIN=管理员 */
    private String role = "CUSTOMER";
    /** 用户类型：NORMAL=普通用户，STUDENT=学生，SENIOR=长者 */
    private String userType = "NORMAL";
    /** 注册时间 */
    private LocalDateTime registrationDate;
    /** 是否激活 */
    private Boolean isActive = true;

    // ============ 支付安全相关字段 ============

    /** 支付密码（BCrypt加密存储，可选） */
    private String paymentPassword;

    /** 银行卡Token（用于存储卡号Token，避免明文存储） */
    private String cardToken;

    /** 卡号后4位（用于显示） */
    private String cardLast4;

    /** 卡类型（VISA, MASTERCARD等） */
    private String cardType;

    /** 是否已绑定银行卡 */
    private Boolean hasCard = false;
}
