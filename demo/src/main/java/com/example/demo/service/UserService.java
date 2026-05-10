package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import java.util.List;

/**
 * 用户服务接口
 * 定义用户相关的业务操作
 */
public interface UserService {
    List<User> findAll();
    User findById(Long id);
    User findByUsername(String username);
    boolean save(User user);
    boolean update(User user);
    boolean deleteById(Long id);
    User register(RegisterRequest request);
    User login(LoginRequest request);
    /**
     * 验证邮箱格式是否合法
     * @param email 邮箱地址
     * @return 是否合法
     */
    boolean isValidEmail(String email);
}
