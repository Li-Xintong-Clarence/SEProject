package com.example.demo.mapper;

import com.example.demo.entity.Booking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 预订Mapper接口
 * 定义预订相关的数据库操作方法
 */
@Mapper
public interface BookingMapper {
    /**
     * 插入预订记录
     * @param booking 预订对象
     * @return 影响的行数
     */
    int insert(Booking booking);

    /**
     * 根据ID查询预订
     * @param id 预订ID
     * @return 预订对象
     */
    Booking findById(@Param("id") Long id);

    /**
     * 查询用户的所有预订
     * @param userId 用户ID
     * @return 预订列表
     */
    List<Booking> findByUserId(@Param("userId") Long userId);

    /**
     * 查询所有预订
     * @return 所有预订列表
     */
    List<Booking> findAll();

    /**
     * 更新预订信息
     * @param booking 预订对象
     * @return 影响的行数
     */
    int update(Booking booking);

    /**
     * 更新预订状态
     * @param id 预订ID
     * @param status 新状态
     * @return 影响的行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 查询按租用选项分组的周收入
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 收入数据列表
     */
    List<Map<String, Object>> findWeeklyIncomeByOption(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 查询每日收入
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日收入数据列表
     */
    List<Map<String, Object>> findDailyIncome(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
