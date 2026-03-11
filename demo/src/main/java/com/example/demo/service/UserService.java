package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.vo.LoginRequest;
import com.example.demo.vo.RegisterRequest;
import java.util.List;

/**
 * 用户服务接口
 * 定义用户相关的业务逻辑方法
 */
public interface UserService {
    /**
     * 获取所有用户（管理员功能）
     * @return 用户列表
     */
    List<User> findAll();

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户对象，不存在返回null
     */
    User findById(Long id);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象，不存在返回null
     */
    User findByUsername(String username);

    /**
     * 添加用户（管理员功能）
     * @param user 用户对象
     * @return 是否添加成功
     */
    boolean save(User user);

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 是否更新成功
     */
    boolean update(User user);

    /**
     * 删除用户（管理员功能）
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);

    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册成功返回用户对象，失败返回null
     */
    User register(RegisterRequest request);

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录成功返回用户对象，失败返回null
     */
    User login(LoginRequest request);
}
