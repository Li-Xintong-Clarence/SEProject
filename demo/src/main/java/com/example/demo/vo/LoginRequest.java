package com.example.demo.vo;

import lombok.Data;

/**
 * 登录请求类
 * 用于用户登录的请求参数
 */
@Data
public class LoginRequest {
    private String username;  // 用户名
    private String password; // 密码
}
