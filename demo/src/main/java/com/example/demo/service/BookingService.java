package com.example.demo.service;

import com.example.demo.entity.Booking;
import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
 * 定义订单（租赁）相关的业务操作
 */
public interface BookingService {
    /**
     * 查询所有订单（管理员）
     */
    List<Booking> findAll();
    /**
     * 查询用户的订单列表
     */
    List<Booking> findByUserId(Long userId);
    /**
     * 根据ID查询订单
     */
    Booking findById(Long id);
    /**
     * 创建新订单
     */
    boolean save(Booking booking);
    /**
     * 更新订单信息
     */
    boolean update(Booking booking);
    /**
     * 删除订单
     */
    boolean deleteById(Long id);
    /**
     * 延长租期
     * @param id 订单ID
     * @param hireOption 延长的时长选项（1hr, 4hr, 1day, 1week）
     */
    boolean extendBooking(Long id, String hireOption);
    /**
     * 取消订单
     */
    boolean cancelBooking(Long id);
    /**
     * 支付订单
     */
    boolean payBooking(Long id);
    /**
     * 获取用户统计信息（订单数、总消费、总时长）
     */
    Map<String, Object> getUserStats(Long userId);
    /**
     * 还车（结束骑行）
     * @param id 订单ID
     */
    boolean returnScooter(Long id);
}
