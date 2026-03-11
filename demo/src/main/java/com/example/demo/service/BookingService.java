package com.example.demo.service;

import com.example.demo.entity.Booking;
import com.example.demo.vo.BookingRequest;
import com.example.demo.vo.DailyIncomeResponse;
import com.example.demo.vo.WeeklyIncomeResponse;
import java.util.List;

/**
 * 预订服务接口
 * 定义预订相关的业务逻辑方法（ID5：预订滑板车）
 */
public interface BookingService {
    /**
     * 创建预订
     * @param userId 用户ID
     * @param request 预订请求（包含滑板车ID和租用选项代码）
     * @return 创建的预订对象
     */
    Booking createBooking(Long userId, BookingRequest request);

    /**
     * 根据ID查询预订
     * @param id 预订ID
     * @return 预订对象，不存在返回null
     */
    Booking findById(Long id);

    /**
     * 查询用户的所有预订
     * @param userId 用户ID
     * @return 预订列表
     */
    List<Booking> findByUserId(Long userId);

    /**
     * 查询所有预订（管理员功能）
     * @return 所有预订列表
     */
    List<Booking> findAll();

    /**
     * 取消预订
     * @param bookingId 预订ID
     * @param userId 用户ID（验证是否为自己的预订）
     * @return 是否取消成功
     */
    boolean cancelBooking(Long bookingId, Long userId);

    /**
     * 延长预订时间
     * @param bookingId 预订ID
     * @param userId 用户ID
     * @param hireOptionCode 新的租用选项代码
     * @return 是否延长成功
     */
    boolean extendBooking(Long bookingId, Long userId, String hireOptionCode);

    /**
     * 获取周收入统计（ID19：按租用选项查看周收入）
     * @return 周收入统计数据
     */
    WeeklyIncomeResponse getWeeklyIncome();

    /**
     * 获取日收入统计（ID20：按天查看收入）
     * @return 日收入统计数据
     */
    DailyIncomeResponse getDailyIncome();
}
