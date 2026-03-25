package com.example.demo.service;

import com.example.demo.entity.Pricing;
import java.util.List;

/**
 * 价格配置服务接口
 * 定义租赁价格相关的业务操作
 */
public interface PricingService {
    /**
     * 查询所有价格配置
     */
    List<Pricing> findAll();
    /**
     * 根据ID查询价格配置
     */
    Pricing findById(Long id);
    /**
     * 保存价格配置（新增）
     */
    boolean save(Pricing pricing);
    /**
     * 更新价格配置
     */
    boolean update(Pricing pricing);
    /**
     * 删除价格配置
     */
    boolean deleteById(Long id);
}
