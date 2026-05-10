package com.example.demo.dto;

import lombok.Data;

/**
 * 注册请求DTO
 * 用于用户注册接口的参数接收
 */
@Data
public class RegisterRequest {
    /** 用户名（唯一） */
    private String username;
    /** 密码 */
    private String password;
    /** 邮箱 */
    private String email;
    /** 手机号 */
    private String phone;
}
