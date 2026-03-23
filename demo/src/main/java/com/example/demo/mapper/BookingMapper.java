package com.example.demo.mapper;

import com.example.demo.entity.Booking;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 订单Mapper接口
 * 对应数据库booking表，使用MyBatis注解方式执行SQL
 */
@Mapper
public interface BookingMapper {
    @Select("SELECT * FROM booking")
    List<Booking> findAll();

    @Select("SELECT * FROM booking WHERE user_id = #{userId}")
    List<Booking> findByUserId(Long userId);

    @Select("SELECT * FROM booking WHERE id = #{id}")
    Booking findById(Long id);

    @Insert("INSERT INTO booking(user_id, scooter_id, hire_option, start_time, end_time, total_cost, status, created_at, confirmation_code) " +
            "VALUES(#{userId}, #{scooterId}, #{hireOption}, #{startTime}, #{endTime}, #{totalCost}, #{status}, #{createdAt}, #{confirmationCode})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Booking booking);

    @Update("UPDATE booking SET user_id=#{userId}, scooter_id=#{scooterId}, hire_option=#{hireOption}, " +
            "start_time=#{startTime}, end_time=#{endTime}, total_cost=#{totalCost}, status=#{status}, confirmation_code=#{confirmationCode} WHERE id=#{id}")
    int update(Booking booking);

    @Delete("DELETE FROM booking WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) FROM booking WHERE user_id = #{userId}")
    int countByUserId(Long userId);

    @Select("SELECT COALESCE(SUM(total_cost), 0) FROM booking WHERE user_id = #{userId} AND status IN ('PAID', 'COMPLETED')")
    Double sumTotalCostByUserId(Long userId);
}
