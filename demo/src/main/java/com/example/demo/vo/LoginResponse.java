package com.example.demo.vo;

import lombok.Data;

/**
 * 登录响应类
 * 用于返回登录结果的响应数据
 */
@Data
public class LoginResponse {
    private String token;      // JWT令牌
    private String username;  // 用户名
    private String role;      // 角色：CUSTOMER或ADMIN
    private Long userId;      // 用户ID

    public LoginResponse(String token, String username, String role, Long userId) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.userId = userId;
    }
}
