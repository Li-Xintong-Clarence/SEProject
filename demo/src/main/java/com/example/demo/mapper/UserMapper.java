package com.example.demo.mapper;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 用户Mapper接口
 * 对应数据库users表，使用MyBatis注解方式执行SQL
 */
@Mapper
public interface UserMapper {
    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * 查询所有用户
     */
    @Select("SELECT * FROM users")
    List<User> findAll();

    /**
     * 插入新用户
     * useGeneratedKeys: 自动生成主键
     * keyProperty: 将生成的主键赋值给user对象的id属性
     */
    @Insert("INSERT INTO users(username, password, email, phone, role, registration_date, is_active) " +
            "VALUES(#{username}, #{password}, #{email}, #{phone}, #{role}, #{registrationDate}, #{isActive})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 更新用户信息
     */
    @Update("UPDATE users SET username=#{username}, password=#{password}, email=#{email}, " +
            "phone=#{phone}, role=#{role}, is_active=#{isActive} WHERE id=#{id}")
    int update(User user);

    /**
     * 根据ID删除用户
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(Long id);
}
