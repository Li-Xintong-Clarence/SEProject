package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.User;
import com.example.demo.service.BookingService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 * 处理用户信息查看、修改、删除等操作
 * 路径: /api/users/*
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    /**
     * 获取所有用户列表（管理员）
     * GET /api/users
     */
    @GetMapping
    public Result<List<User>> findAll() {
        return Result.success(userService.findAll());
    }

    /**
     * 根据ID获取用户信息
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public Result<User> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("User not found");
    }

    /**
     * 获取当前登录用户信息
     * GET /api/users/me
     * 需要登录，从JWT中提取用户ID
     */
    @GetMapping("/me")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.findById(userId);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("User not found");
    }

    /**
     * 获取当前用户的统计数据（订单数、总消费等）
     * GET /api/users/me/stats
     */
    @GetMapping("/me/stats")
    public Result<Map<String, Object>> getUserStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> stats = bookingService.getUserStats(userId);
        return Result.success(stats);
    }

    /**
     * 创建新用户
     * POST /api/users
     */
    @PostMapping
    public Result<String> add(@RequestBody User user) {
        if (!userService.isValidEmail(user.getEmail())) {
            return Result.error("Invalid email format");
        }
        if (userService.save(user)) {
            return Result.success("User created successfully");
        }
        return Result.error("Failed to create user");
    }

    /**
     * 更新用户信息
     * PUT /api/users
     */
    @PutMapping
    public Result<String> update(@RequestBody User user) {
        if (user.getEmail() != null && !userService.isValidEmail(user.getEmail())) {
            return Result.error("Invalid email format");
        }
        if (userService.update(user)) {
            return Result.success("User updated successfully");
        }
        return Result.error("Failed to update user");
    }

    /**
     * 删除用户
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        if (userService.deleteById(id)) {
            return Result.success("User deleted successfully");
        }
        return Result.error("Failed to delete user");
    }
}
