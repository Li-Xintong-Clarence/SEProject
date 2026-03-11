package com.example.demo.service.impl;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import com.example.demo.util.PasswordEncoder;
import com.example.demo.vo.LoginRequest;
import com.example.demo.vo.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 * 实现用户相关的业务逻辑
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取所有用户
     */
    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    /**
     * 根据ID查询用户
     */
    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    /**
     * 根据用户名查询用户
     */
    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    /**
     * 添加用户
     */
    @Override
    public boolean save(User user) {
        return userMapper.insert(user) > 0;
    }

    /**
     * 更新用户信息
     */
    @Override
    public boolean update(User user) {
        return userMapper.update(user) > 0;
    }

    /**
     * 删除用户
     */
    @Override
    public boolean deleteById(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    /**
     * 用户注册
     * 1. 检查用户名是否已存在
     * 2. 创建新用户（密码加密）
     * 3. 设置默认角色为CUSTOMER
     */
    @Override
    public User register(RegisterRequest request) {
        // Check if username already exists
        User existingUser = userMapper.findByUsername(request.getUsername());
        if (existingUser != null) {
            return null;
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole() != null ? request.getRole() : "CUSTOMER");
        user.setRegistrationDate(LocalDateTime.now());
        user.setIsActive(true);

        if (userMapper.insert(user) > 0) {
            // Return user without password
            user.setPassword(null);
            return user;
        }
        return null;
    }

    /**
     * 用户登录
     * 1. 根据用户名查询用户
     * 2. 验证密码
     * 3. 返回用户信息（密码置空）
     */
    @Override
    public User login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            return null;
        }

        // Check password
        if (!PasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            return null;
        }

        // Return user without password
        user.setPassword(null);
        return user;
    }
}
