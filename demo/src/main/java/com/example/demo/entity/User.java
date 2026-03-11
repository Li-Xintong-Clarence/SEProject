package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 表示系统的用户信息
 */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String role;          // 角色：CUSTOMER（客户）, ADMIN（管理员）
    private LocalDateTime registrationDate;
    private Boolean isActive;     // 是否激活
}
