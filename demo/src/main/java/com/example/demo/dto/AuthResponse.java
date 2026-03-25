package com.example.demo.dto;

import com.example.demo.entity.User;
import lombok.Data;

/**
 * 认证响应DTO
 * 用于登录/注册成功后返回给客户端的数据
 */
@Data
public class AuthResponse {
    /** JWT令牌（后续请求需放在Header中） */
    private String token;
    /** 用户信息 */
    private User user;
}
