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
    /** 注册时间 */
    private LocalDateTime registrationDate;
    /** 是否激活 */
    private Boolean isActive = true;
}
