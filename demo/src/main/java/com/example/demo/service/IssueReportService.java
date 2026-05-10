package com.example.demo.service;

import com.example.demo.entity.IssueReport;
import com.example.demo.vo.IssueReportRequest;
import java.util.List;

/**
 * 问题报告服务接口
 * 定义问题报告相关的业务逻辑方法（ID13/14：报告问题）
 */
public interface IssueReportService {
    /**
     * 创建问题报告
     * @param userId 用户ID
     * @param request 问题报告请求
     * @return 创建的问题报告对象
     */
    IssueReport create(Long userId, IssueReportRequest request);

    /**
     * 查询所有问题报告（管理员功能）
     * @return 所有问题报告列表
     */
    List<IssueReport> findAll();

    /**
     * 查询用户提交的问题报告
     * @param userId 用户ID
     * @return 问题报告列表
     */
    List<IssueReport> findByUserId(Long userId);
}
