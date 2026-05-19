package com.example.demo.mapper;

import com.example.demo.entity.Booking;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

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

    @Insert("INSERT INTO booking(user_id, scooter_id, hire_option, start_time, end_time, total_cost, status, created_at, confirmation_code, start_depot_id, end_depot_id, booking_type, guest_name, guest_phone, guest_email) " +
            "VALUES(#{userId}, #{scooterId}, #{hireOption}, #{startTime}, #{endTime}, #{totalCost}, #{status}, #{createdAt}, #{confirmationCode}, #{startDepotId}, #{endDepotId}, #{bookingType}, #{guestName}, #{guestPhone}, #{guestEmail})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Booking booking);

    @Update("UPDATE booking SET user_id=#{userId}, scooter_id=#{scooterId}, hire_option=#{hireOption}, " +
            "start_time=#{startTime}, end_time=#{endTime}, total_cost=#{totalCost}, status=#{status}, " +
            "confirmation_code=#{confirmationCode}, start_depot_id=#{startDepotId}, end_depot_id=#{endDepotId}, " +
            "booking_type=#{bookingType}, guest_name=#{guestName}, guest_phone=#{guestPhone}, guest_email=#{guestEmail} WHERE id=#{id}")
    int update(Booking booking);

    @Delete("DELETE FROM booking WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) FROM booking WHERE user_id = #{userId}")
    int countByUserId(Long userId);

    @Select("SELECT COALESCE(SUM(total_cost), 0) FROM booking WHERE user_id = #{userId} AND status IN ('PAID', 'COMPLETED', 'ACTIVE')")
    Double sumTotalCostByUserId(Long userId);

    // ============ 统计查询 ============

    @Select("SELECT COUNT(*) FROM booking")
    int countBookings();

    @Select("SELECT COUNT(*) FROM booking WHERE status = #{status}")
    int countByStatus(String status);

    @Select("SELECT COALESCE(SUM(total_cost), 0) FROM booking WHERE status IN ('PAID', 'COMPLETED')")
    Double sumTotalIncome();

    @Select("SELECT COALESCE(SUM(total_cost), 0) FROM booking WHERE DATE(created_at) = CURDATE() AND status IN ('PAID', 'COMPLETED')")
    Double sumTodayIncome();

    @Select("SELECT COALESCE(SUM(total_cost), 0) FROM booking WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND status IN ('PAID', 'COMPLETED')")
    Double sumWeekIncome();

    @Select("SELECT COALESCE(AVG(TIMESTAMPDIFF(MINUTE, start_time, end_time)), 0) FROM booking WHERE status = 'COMPLETED' AND end_time IS NOT NULL")
    Double avgRideDuration();

    @Select("SELECT COALESCE(SUM(TIMESTAMPDIFF(HOUR, start_time, end_time)), 0) FROM booking " +
            "WHERE user_id = #{userId} AND created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "AND status IN ('PAID', 'COMPLETED') AND end_time IS NOT NULL")
    Integer getUserWeeklyHours(Long userId);

    @Select("SELECT DATE(created_at) as date, SUM(total_cost) as income FROM booking WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND status IN ('PAID', 'COMPLETED') GROUP BY DATE(created_at) ORDER BY date")
    List<Map<String, Object>> getDailyIncome();

    @Select("SELECT hire_option, SUM(total_cost) as income FROM booking WHERE status IN ('PAID', 'COMPLETED') GROUP BY hire_option")
    List<Map<String, Object>> getWeeklyIncomeByOption();

    @Select("SELECT status, COUNT(*) as count FROM booking GROUP BY status")
    List<Map<String, Object>> getBookingStatusDistribution();

    @Select("SELECT HOUR(start_time) as hour, COUNT(*) as count FROM booking GROUP BY HOUR(start_time) ORDER BY hour")
    List<Map<String, Object>> getPeakHours();

    @Select("SELECT user_id, u.username, u.email, COUNT(*) as booking_count FROM booking b JOIN users u ON b.user_id = u.id GROUP BY user_id, u.username, u.email ORDER BY booking_count DESC LIMIT #{limit}")
    List<Map<String, Object>> getTopActiveUsers(@Param("limit") int limit);

    // 工单/故障统计（需要issue_reports表）
    @Select("SELECT COUNT(*) FROM issue_report WHERE status IN ('PENDING', 'OPEN')")
    int countPendingIssues();

    // ID20: 获取一周内每天的订单数量（按星期统计热门租赁日）
    @Select("SELECT DAYOFWEEK(created_at) as day_of_week, COUNT(*) as count " +
            "FROM booking " +
            "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY DAYOFWEEK(created_at) " +
            "ORDER BY day_of_week")
    List<Map<String, Object>> getBookingsByDayOfWeek();

    // ID20: 获取一周内每天各租期的收入统计
    @Select("SELECT DATE(created_at) as date, hire_option, SUM(total_cost) as income " +
            "FROM booking " +
            "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "AND status IN ('PAID', 'COMPLETED') " +
            "GROUP BY DATE(created_at), hire_option " +
            "ORDER BY date, hire_option")
    List<Map<String, Object>> getDailyIncomeByOption();
}
