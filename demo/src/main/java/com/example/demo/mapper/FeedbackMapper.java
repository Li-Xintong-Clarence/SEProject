package com.example.demo.mapper;

import com.example.demo.entity.Feedback;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 反馈Mapper接口
 * 对应数据库feedback表，使用MyBatis注解方式执行SQL
 */
@Mapper
public interface FeedbackMapper {
    /**
     * 查询所有反馈
     */
    @Select("SELECT * FROM feedback")
    List<Feedback> findAll();
    /**
     * 查询用户的反馈列表
     */
    @Select("SELECT * FROM feedback WHERE user_id = #{userId}")
    List<Feedback> findByUserId(Long userId);
    /**
     * 按优先级查询反馈
     */
    @Select("SELECT * FROM feedback WHERE priority = #{priority}")
    List<Feedback> findByPriority(String priority);
    /**
     * 根据ID查询反馈
     */
    @Select("SELECT * FROM feedback WHERE id = #{id}")
    Feedback findById(Long id);
    /**
     * 插入新反馈
     */
    @Insert("INSERT INTO feedback(user_id, booking_id, description, status, priority, created_at) " +
            "VALUES(#{userId}, #{bookingId}, #{description}, #{status}, #{priority}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Feedback feedback);
    /**
     * 更新反馈信息
     */
    @Update("UPDATE feedback SET description=#{description}, status=#{status}, priority=#{priority}, " +
            "admin_response=#{adminResponse}, resolved_at=#{resolvedAt} WHERE id=#{id}")
    int update(Feedback feedback);
    /**
     * 删除反馈
     */
    @Delete("DELETE FROM feedback WHERE id = #{id}")
    int deleteById(Long id);
}
