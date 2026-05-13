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

    /**
     * 根据ID查询问题报告
     * @param id 问题报告ID
     * @return 问题报告对象
     */
    IssueReport findById(Long id);

    /**
     * 按优先级查询问题报告（ID14: 查看高优先级问题）
     * @param priority 优先级：HIGH, NORMAL, LOW
     * @return 问题报告列表
     */
    List<IssueReport> findByPriority(String priority);

    /**
     * 更新问题报告
     * @param issue 问题报告对象
     * @return 更新是否成功
     */
    boolean update(IssueReport issue);
}
