package com.example.demo.dto;

import lombok.Data;

/**
 * 登录请求DTO
 * 用于用户登录接口的参数接收
 */
@Data
public class LoginRequest {
    /** 用户名 */
    private String username;
    /** 密码 */
    private String password;
}
