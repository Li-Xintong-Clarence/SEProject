package com.example.demo.mapper;

import com.example.demo.entity.Scooter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 滑板车Mapper接口
 * 定义滑板车相关的数据库操作方法
 */
@Mapper
public interface ScooterMapper {
    /**
     * 查询所有滑板车
     * @return 所有滑板车列表
     */
    List<Scooter> findAll();

    /**
     * 根据ID查询滑板车
     * @param id 滑板车ID
     * @return 滑板车对象
     */
    Scooter findById(@Param("id") Long id);

    /**
     * 查询所有可用的滑板车
     * @return 可用滑板车列表
     */
    List<Scooter> findAvailable();

    /**
     * 插入滑板车
     * @param scooter 滑板车对象
     * @return 影响的行数
     */
    int insert(Scooter scooter);

    /**
     * 更新滑板车信息
     * @param scooter 滑板车对象
     * @return 影响的行数
     */
    int update(Scooter scooter);

    /**
     * 删除滑板车
     * @param id 滑板车ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") Long id);
}
