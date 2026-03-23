package com.example.demo.service;

import com.example.demo.entity.Feedback;
import java.util.List;

/**
 * 反馈服务接口
 * 定义用户意见反馈相关的业务操作
 */
public interface FeedbackService {
    /**
     * 查询所有反馈（管理员）
     */
    List<Feedback> findAll();
    /**
     * 查询用户的反馈列表
     */
    List<Feedback> findByUserId(Long userId);
    /**
     * 按优先级查询反馈
     * @param priority 优先级（HIGH, MEDIUM, LOW）
     */
    List<Feedback> findByPriority(String priority);
    /**
     * 根据ID查询反馈
     */
    Feedback findById(Long id);
    /**
     * 提交反馈
     */
    boolean save(Feedback feedback);
    /**
     * 更新反馈信息
     */
    boolean update(Feedback feedback);
    /**
     * 删除反馈
     */
    boolean deleteById(Long id);
}
