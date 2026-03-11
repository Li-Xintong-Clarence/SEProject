package com.example.demo.vo;

import lombok.Data;

/**
 * 注册请求类
 * 用于用户注册的请求参数
 */
@Data
public class RegisterRequest {
    private String username;  // 用户名
    private String password; // 密码
    private String email;   // 邮箱
    private String phone;   // 手机号
    private String role;   // 角色：CUSTOMER或ADMIN
}
