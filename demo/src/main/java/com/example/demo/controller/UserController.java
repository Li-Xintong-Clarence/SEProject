package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import com.example.demo.vo.LoginRequest;
import com.example.demo.vo.LoginResponse;
import com.example.demo.vo.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户列表
     * @return 所有用户的列表
     */
    @GetMapping
    public Result<List<User>> findAll() {
        return Result.success(userService.findAll());
    }

    /**
     * 根据用户ID查询单个用户信息
     * @param id 用户ID
     * @return 用户信息（密码已置空）
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
     * 用户注册接口
     * @param request 包含用户名、密码、邮箱、手机号等注册信息
     * @return 注册成功返回用户信息，失败返回错误信息
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return Result.error("Username and password are required");
        }
        User user = userService.register(request);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("Username already exists");
    }

    /**
     * 用户登录接口
     * @param request 包含用户名和密码
     * @return 登录成功返回JWT令牌和用户信息，失败返回错误信息
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return Result.error("Username and password are required");
        }
        User user = userService.login(request);
        if (user != null) {
            String token = JwtUtil.generateToken(user.getUsername(), user.getRole());
            LoginResponse response = new LoginResponse(token, user.getUsername(), user.getRole(), user.getId());
            return Result.success(response);
        }
        return Result.error("Invalid username or password");
    }

    /**
     * 管理员添加用户（手动创建用户）
     * @param user 用户信息
     * @return 创建成功返回成功信息，失败返回错误信息
     */
    @PostMapping
    public Result<String> add(@RequestBody User user) {
        if (userService.save(user)) {
            return Result.success("User created successfully");
        }
        return Result.error("Failed to create user");
    }

    /**
     * 更新用户信息
     * @param user 用户信息（包含ID）
     * @return 更新成功返回成功信息，失败返回错误信息
     */
    @PutMapping
    public Result<String> update(@RequestBody User user) {
        if (userService.update(user)) {
            return Result.success("User updated successfully");
        }
        return Result.error("Failed to update user");
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除成功返回成功信息，失败返回错误信息
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        if (userService.deleteById(id)) {
            return Result.success("User deleted successfully");
        }
        return Result.error("Failed to delete user");
    }
}
