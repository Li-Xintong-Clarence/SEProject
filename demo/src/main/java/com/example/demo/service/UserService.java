package com.example.demo.service;

import com.example.demo.entity.User;
import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Long id);
    User findByUsername(String username);
    boolean save(User user);
    boolean update(User user);
    boolean deleteById(Long id);
}
