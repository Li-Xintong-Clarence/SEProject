package com.example.demo.mapper;

import com.example.demo.entity.IssueReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 问题报告Mapper接口
 * 定义问题报告相关的数据库操作方法（ID13/14：报告问题）
 */
@Mapper
public interface IssueReportMapper {
    /**
     * 插入问题报告
     * @param report 问题报告对象
     * @return 影响的行数
     */
    int insert(IssueReport report);

    /**
     * 根据ID查询问题报告
     * @param id 问题报告ID
     * @return 问题报告对象
     */
    IssueReport findById(@Param("id") Long id);

    /**
     * 查询所有问题报告
     * @return 所有问题报告列表
     */
    List<IssueReport> findAll();

    /**
     * 查询用户提交的问题报告
     * @param userId 用户ID
     * @return 问题报告列表
     */
    List<IssueReport> findByUserId(@Param("userId") Long userId);

    /**
     * 更新问题报告
     * @param report 问题报告对象
     * @return 影响的行数
     */
    int update(IssueReport report);
}
