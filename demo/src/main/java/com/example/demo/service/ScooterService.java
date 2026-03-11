package com.example.demo.service;

import com.example.demo.entity.Scooter;
import java.util.List;

/**
 * 滑板车服务接口
 * 定义滑板车相关的业务逻辑方法
 */
public interface ScooterService {
    /**
     * 获取所有滑板车
     * @return 所有滑板车列表
     */
    List<Scooter> findAll();

    /**
     * 根据ID查询滑板车
     * @param id 滑板车ID
     * @return 滑板车对象，不存在返回null
     */
    Scooter findById(Long id);

    /**
     * 获取所有可用的滑板车
     * @return 可用滑板车列表
     */
    List<Scooter> findAvailable();

    /**
     * 添加滑板车（管理员功能）
     * @param scooter 滑板车对象
     * @return 是否添加成功
     */
    boolean save(Scooter scooter);

    /**
     * 更新滑板车信息
     * @param scooter 滑板车对象
     * @return 是否更新成功
     */
    boolean update(Scooter scooter);

    /**
     * 删除滑板车（管理员功能）
     * @param id 滑板车ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);
}
