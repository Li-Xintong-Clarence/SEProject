package com.example.demo.service.service;

import com.example.demo.service.dto.RegisterRequest;
import com.example.demo.service.entity.User;
import com.example.demo.service.mapper.UserMapper;
import com.example.demo.service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User dummyUser;

    @BeforeEach
    void setUp() {
        // 初始化一个“不完整”的用户，故意不设置 Role 和 isActive
        dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setUsername("testUser");
    }

    @Test
    void testSave_SetsDefaultValuesAndSucceeds() {
        // 模拟数据库插入成功
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // 执行保存操作
        boolean result = userService.save(dummyUser);

        // 断言验证 1：保存成功
        assertTrue(result);

        // 断言验证 2：系统是否正确填充了默认值 (这一步是向老师展示代码质量的关键)
        assertEquals("CUSTOMER", dummyUser.getRole());
        assertTrue(dummyUser.getIsActive());
        assertNotNull(dummyUser.getRegistrationDate());

        // 验证确实调用了 insert 方法
        verify(userMapper, times(1)).insert(dummyUser);
    }

    @Test
    void testRegister_FailsWhenUsernameExists() {
        // 准备注册请求
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existingUser");

        // 模拟数据库中已经存在该用户名
        when(userMapper.findByUsername("existingUser")).thenReturn(new User());

        // 执行注册
        User result = userService.register(request);

        // 验证结果：注册失败返回 null
        assertNull(result);

        // 验证防线：绝对没有调用数据库的 insert 方法去插入重复数据
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void testFindById_Success() {
        // 模拟按ID查找用户
        when(userMapper.findById(1L)).thenReturn(dummyUser);

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        verify(userMapper, times(1)).findById(1L);
    }
}