package com.example.demo.service.impl;

import com.example.demo.entity.IssueReport;
import com.example.demo.mapper.IssueReportMapper;
import com.example.demo.service.IssueReportService;
import com.example.demo.vo.IssueReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 问题报告服务实现类
 * 实现问题报告相关的业务逻辑（ID13/14：报告问题）
 */
@Service
public class IssueReportServiceImpl implements IssueReportService {

    @Autowired
    private IssueReportMapper issueReportMapper;

    /**
     * 创建问题报告
     * 设置初始状态为PENDING，优先级为NORMAL
     */
    @Override
    public IssueReport create(Long userId, IssueReportRequest request) {
        IssueReport report = new IssueReport();
        report.setUserId(userId);
        report.setScooterId(request.getScooterId());
        report.setDescription(request.getDescription());
        report.setStatus("PENDING");
        report.setPriority("NORMAL");
        issueReportMapper.insert(report);
        return report;
    }

    /**
     * 查询所有问题报告
     */
    @Override
    public List<IssueReport> findAll() {
        return issueReportMapper.findAll();
    }

    /**
     * 查询用户提交的问题报告
     */
    @Override
    public List<IssueReport> findByUserId(Long userId) {
        return issueReportMapper.findByUserId(userId);
    }
}
