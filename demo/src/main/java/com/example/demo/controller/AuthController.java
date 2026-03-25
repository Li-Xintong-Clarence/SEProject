package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户注册、登录、管理员登录等认证相关请求
 * 路径: /auth/*
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户注册接口
     * POST /auth/register
     * 参数: username, password, email, phone
     * 返回: token + 用户信息
     */
    @PostMapping("/register")
    public Result<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录接口
     * POST /auth/login
     * 参数: username, password
     * 返回: token + 用户信息
     */
    @PostMapping("/login")
    public Result<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return Result.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            String msg = e.getMessage();
            if (msg == null || msg.isEmpty()) {
                msg = cause != null ? cause.getMessage() : e.getClass().getSimpleName();
            }
            return Result.error(msg);
        }
    }

    /**
     * 管理员登录接口
     * POST /auth/admin/login
     * 参数: username, password
     * 返回: token + 用户信息（角色为ADMIN）
     */
    @PostMapping("/admin/login")
    public Result<AuthResponse> adminLogin(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.adminLogin(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 检查当前用户是否为管理员
     * GET /auth/check-admin
     * 需要登录（携带 Authorization header）
     * 返回: 是否为管理员
     */
    @GetMapping("/check-admin")
    public Result<Boolean> checkAdmin(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error("Missing or invalid token");
        }
        String token = authHeader.substring(7);
        try {
            String role = com.example.demo.util.JwtUtil.getRole(token);
            return Result.success("ADMIN".equals(role));
        } catch (Exception e) {
            return Result.error("Invalid token");
        }
    }
}
