package com.example.demo.mapper;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

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
    @Insert("INSERT INTO users(username, password, email, phone, role, user_type, registration_date, is_active) " +
            "VALUES(#{username}, #{password}, #{email}, #{phone}, #{role}, #{userType}, #{registrationDate}, #{isActive})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 更新用户信息
     */
    @Update("UPDATE users SET username=#{username}, password=#{password}, email=#{email}, " +
            "phone=#{phone}, role=#{role}, user_type=#{userType}, is_active=#{isActive} WHERE id=#{id}")
    int update(User user);

    /**
     * 更新用户状态
     */
    @Update("UPDATE users SET is_active=#{isActive} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("isActive") boolean isActive);

    /**
     * 根据ID删除用户
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(Long id);

    // ============ 统计查询 ============

    @Select("SELECT COUNT(*) FROM users WHERE role != 'ADMIN'")
    int countUsers();

    @Select("SELECT COUNT(*) FROM users WHERE DATE(registration_date) = CURDATE() AND role != 'ADMIN'")
    int countTodayNewUsers();

    @Select("SELECT COUNT(*) FROM users WHERE registration_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND role != 'ADMIN'")
    int countWeekNewUsers();

    @Select("SELECT COUNT(*) FROM users WHERE registration_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) AND role != 'ADMIN'")
    int countMonthNewUsers();

    @Select("SELECT DATE(registration_date) as date, COUNT(*) as count FROM users WHERE registration_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND role != 'ADMIN' GROUP BY DATE(registration_date) ORDER BY date")
    List<Map<String, Object>> getDailyNewUsers();
}
