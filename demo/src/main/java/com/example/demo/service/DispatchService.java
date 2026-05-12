package com.example.demo.service;

import com.example.demo.entity.Hotspot;
import com.example.demo.entity.Scooter;
import java.util.List;
import java.util.Map;

/**
 * 调度服务接口
 * 定义滑板车动态调度相关操作
 */
public interface DispatchService {
    
    /**
     * 根据热门区域权重，动态重新分布滑板车
     * @return 调度结果信息
     */
    Map<String, Object> redistributeScooters();

    /**
     * 获取所有热门区域
     */
    List<Hotspot> getAllHotspots();

    /**
     * 获取活跃的热门区域
     */
    List<Hotspot> getActiveHotspots();

    /**
     * 添加热门区域
     */
    boolean addHotspot(Hotspot hotspot);

    /**
     * 更新热门区域权重（模拟使用频率变化）
     */
    boolean updateHotspotWeight(Long id, Integer weight);

    /**
     * 删除热门区域
     */
    boolean deleteHotspot(Long id);

    /**
     * 模拟热门区域使用（增加该区域权重）
     */
    boolean simulateUsage(Long hotspotId);

    /**
     * 获取调度统计信息
     */
    Map<String, Object> getDispatchStats();
}
