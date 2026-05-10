package com.example.demo.service.impl;

import com.example.demo.entity.Feedback;
import com.example.demo.mapper.FeedbackMapper;
import com.example.demo.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 反馈服务实现类
 * 实现用户意见反馈相关的具体业务逻辑
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    public List<Feedback> findAll() {
        return feedbackMapper.findAll();
    }

    @Override
    public List<Feedback> findByUserId(Long userId) {
        return feedbackMapper.findByUserId(userId);
    }

    @Override
    public List<Feedback> findByPriority(String priority) {
        return feedbackMapper.findByPriority(priority);
    }

    @Override
    public Feedback findById(Long id) {
        return feedbackMapper.findById(id);
    }

    /**
     * 提交反馈
     * 自动设置状态为OPEN，创建时间为当前时间
     */
    @Override
    public boolean save(Feedback feedback) {
        feedback.setStatus("OPEN");
        feedback.setCreatedAt(LocalDateTime.now());
        return feedbackMapper.insert(feedback) > 0;
    }

    @Override
    public boolean update(Feedback feedback) {
        return feedbackMapper.update(feedback) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return feedbackMapper.deleteById(id) > 0;
    }
}
