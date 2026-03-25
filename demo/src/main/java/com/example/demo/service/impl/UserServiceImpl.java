package com.example.demo.service.impl;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import com.example.demo.util.PasswordEncoder;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 * 实现用户相关的具体业务逻辑
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public boolean save(User user) {
        if (user.getRegistrationDate() == null) {
            user.setRegistrationDate(LocalDateTime.now());
        }
        if (user.getRole() == null) {
            user.setRole("CUSTOMER");
        }
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        return userMapper.insert(user) > 0;
    }

    @Override
    public boolean update(User user) {
        return userMapper.update(user) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public User register(RegisterRequest request) {
        User existingUser = userMapper.findByUsername(request.getUsername());
        if (existingUser != null) {
            return null;
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole("CUSTOMER");
        user.setRegistrationDate(LocalDateTime.now());
        user.setIsActive(true);

        if (userMapper.insert(user) > 0) {
            user.setPassword(null);
            return user;
        }
        return null;
    }

    @Override
    public User login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            return null;
        }
        if (!PasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            return null;
        }
        user.setPassword(null);
        return user;
    }
}
