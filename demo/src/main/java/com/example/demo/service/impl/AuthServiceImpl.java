package com.example.demo.service.impl;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 * 实现用户注册、登录、管理员登录的具体业务逻辑
 * 密码使用BCrypt加密，生成JWT Token返回
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * 1. 检查用户名是否已存在
     * 2. 使用BCrypt加密密码
     * 3. 设置默认角色为CUSTOMER
     * 4. 保存用户到数据库
     * 5. 生成JWT Token返回
     */
    @Override
    public AuthResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        User existingUser = userService.findByUsername(request.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("Username already exists");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        // 密码使用BCrypt加密存储
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole("CUSTOMER");  // 默认普通用户角色

        // 保存到数据库
        boolean saved = userService.save(user);
        if (!saved) {
            throw new RuntimeException("Failed to create user");
        }

        // 生成JWT Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUser(user);
        return response;
    }

    /**
     * 用户登录
     * 1. 根据用户名查询用户
     * 2. 验证密码（BCrypt比对）
     * 3. 检查角色是否为CUSTOMER
     * 4. 生成JWT Token返回
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        // 根据用户名查询用户
        User user = userService.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("Invalid username or password");
        }
        String storedHash = user.getPassword();
        if (storedHash == null || request.getPassword() == null) {
            throw new RuntimeException("Invalid username or password");
        }
        // 使用BCrypt验证密码
        try {
            if (!BCrypt.checkpw(request.getPassword(), storedHash)) {
                throw new RuntimeException("Invalid username or password");
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid username or password");
        }
        // 检查是否为普通用户（管理员用专门的管理员登录接口）
        if (!"CUSTOMER".equals(user.getRole())) {
            throw new RuntimeException("Use /auth/admin/login for admin access");
        }

        // 生成JWT Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUser(user);
        return response;
    }

    /**
     * 管理员登录
     * 1. 根据用户名查询用户
     * 2. 验证密码（BCrypt比对）
     * 3. 检查角色是否为ADMIN
     * 4. 生成JWT Token返回
     */
    @Override
    public AuthResponse adminLogin(LoginRequest request) {
        // 根据用户名查询用户
        User user = userService.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("Invalid username or password");
        }
        String storedHash = user.getPassword();
        if (storedHash == null || request.getPassword() == null) {
            throw new RuntimeException("Invalid username or password");
        }
        // 使用BCrypt验证密码
        try {
            if (!BCrypt.checkpw(request.getPassword(), storedHash)) {
                throw new RuntimeException("Invalid username or password");
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid username or password");
        }
        // 检查是否为管理员
        if (!"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Not an admin user");
        }

        // 生成JWT Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUser(user);
        return response;
    }
}
