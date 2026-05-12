package com.example.demo.service.impl;

import com.example.demo.entity.Hotspot;
import com.example.demo.entity.Scooter;
import com.example.demo.mapper.HotspotMapper;
import com.example.demo.mapper.ScooterMapper;
import com.example.demo.service.DispatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 调度服务实现类
 * 根据热门区域动态分布滑板车
 */
@Service
public class DispatchServiceImpl implements DispatchService {

    @Autowired
    private HotspotMapper hotspotMapper;

    @Autowired
    private ScooterMapper scooterMapper;

    /**
     * 核心算法：根据热门区域权重重新分布滑板车
     * 
     * 算法思路：
     * 1. 获取所有活跃热门区域和可用滑板车
     * 2. 根据权重计算每个区域应分配的滑板车数量
     * 3. 在每个区域半径内随机生成位置
     * 4. 更新滑板车位置
     */
    @Override
    @Transactional
    public Map<String, Object> redistributeScooters() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> distribution = new ArrayList<>();
        
        try {
            // 1. 获取数据
            List<Hotspot> hotspots = hotspotMapper.findActive();
            List<Scooter> availableScooters = scooterMapper.findAvailable();
            List<Scooter> allScooters = scooterMapper.findAll();
            
            if (hotspots.isEmpty()) {
                result.put("success", false);
                result.put("message", "没有活跃的热门区域");
                return result;
            }
            
            if (allScooters.isEmpty()) {
                result.put("success", false);
                result.put("message", "没有滑板车数据");
                return result;
            }

            // 2. 计算总权重
            int totalWeight = hotspots.stream().mapToInt(Hotspot::getWeight).sum();
            if (totalWeight == 0) {
                // 如果权重都为0，平均分配
                totalWeight = hotspots.size();
                for (Hotspot h : hotspots) {
                    h.setWeight(1);
                }
            }

            // 3. 计算每个区域应分配的滑板车数量
            Map<Long, Integer> hotspotScooterCount = new HashMap<>();
            int remaining = allScooters.size();
            
            for (int i = 0; i < hotspots.size() - 1; i++) {
                Hotspot hotspot = hotspots.get(i);
                int count = (int) Math.round((double) hotspot.getWeight() / totalWeight * allScooters.size());
                count = Math.max(1, count); // 每个区域至少1辆
                hotspotScooterCount.put(hotspot.getId(), count);
                remaining -= count;
            }
            // 最后一个区域分配剩余的
            if (!hotspots.isEmpty()) {
                hotspotScooterCount.put(hotspots.get(hotspots.size() - 1).getId(), 
                    Math.max(1, remaining));
            }

            // 4. 重新分配滑板车
            int index = 0;
            for (Hotspot hotspot : hotspots) {
                int count = hotspotScooterCount.get(hotspot.getId());
                Map<String, Object> distInfo = new HashMap<>();
                distInfo.put("hotspot", hotspot.getName());
                distInfo.put("count", count);
                
                List<String> moved = new ArrayList<>();
                for (int i = 0; i < count && index < allScooters.size(); i++) {
                    Scooter scooter = allScooters.get(index++);
                    
                    // 在区域内随机生成新位置
                    double[] newPos = generateRandomPosition(
                        hotspot.getLatitude(), 
                        hotspot.getLongitude(), 
                        hotspot.getRadius()
                    );
                    
                    // 更新滑板车位置
                    scooter.setLatitude(newPos[0]);
                    scooter.setLongitude(newPos[1]);
                    scooter.setLocation(hotspot.getLocation() + "附近");
                    scooterMapper.update(scooter);
                    
                    moved.add(scooter.getScooterNumber());
                }
                distInfo.put("scooters", moved);
                distribution.add(distInfo);
            }

            result.put("success", true);
            result.put("message", "调度完成");
            result.put("distribution", distribution);
            result.put("totalScooters", allScooters.size());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "调度失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 在圆形区域内随机生成一个位置
     * @param centerLat 中心纬度
     * @param centerLng 中心经度
     * @param radiusMeters 半径（米）
     * @return [纬度, 经度]
     */
    private double[] generateRandomPosition(double centerLat, double centerLng, int radiusMeters) {
        Random random = new Random();
        
        // 生成随机距离（使用平方根使分布更均匀）
        double distance = Math.sqrt(random.nextDouble()) * radiusMeters;
        
        // 生成随机角度
        double angle = random.nextDouble() * 2 * Math.PI;
        
        // 将距离转换为经纬度偏移
        // 1度纬度 ≈ 111km, 1度经度 ≈ 111km * cos(纬度)
        double latOffset = (distance * Math.cos(angle)) / 111000.0;
        double lngOffset = (distance * Math.sin(angle)) / (111000.0 * Math.cos(Math.toRadians(centerLat)));
        
        return new double[] {
            centerLat + latOffset,
            centerLng + lngOffset
        };
    }

    @Override
    public List<Hotspot> getAllHotspots() {
        return hotspotMapper.findAll();
    }

    @Override
    public List<Hotspot> getActiveHotspots() {
        return hotspotMapper.findActive();
    }

    @Override
    public boolean addHotspot(Hotspot hotspot) {
        if (hotspot.getStatus() == null) {
            hotspot.setStatus("ACTIVE");
        }
        if (hotspot.getRadius() == null) {
            hotspot.setRadius(300); // 默认300米
        }
        if (hotspot.getWeight() == null) {
            hotspot.setWeight(10);
        }
        return hotspotMapper.insert(hotspot) > 0;
    }

    @Override
    public boolean updateHotspotWeight(Long id, Integer weight) {
        return hotspotMapper.updateWeight(id, weight) > 0;
    }

    @Override
    public boolean deleteHotspot(Long id) {
        return hotspotMapper.deleteById(id) > 0;
    }

    /**
     * 模拟使用：增加该区域权重，相应减少其他区域权重
     * 模拟真实的"使用频繁的区域会调度更多车"
     */
    @Override
    public boolean simulateUsage(Long hotspotId) {
        List<Hotspot> hotspots = hotspotMapper.findActive();
        Hotspot target = hotspotMapper.findById(hotspotId);
        
        if (target == null) return false;
        
        // 增加目标区域权重
        int increase = 5;
        int newWeight = Math.min(100, target.getWeight() + increase);
        hotspotMapper.updateWeight(hotspotId, newWeight);
        
        // 随机减少其他区域权重（保持总量平衡）
        List<Hotspot> others = hotspots.stream()
            .filter(h -> !h.getId().equals(hotspotId))
            .toList();
        
        if (!others.isEmpty()) {
            Hotspot reduce = others.get(new Random().nextInt(others.size()));
            int reducedWeight = Math.max(1, reduce.getWeight() - increase / others.size());
            hotspotMapper.updateWeight(reduce.getId(), reducedWeight);
        }
        
        return true;
    }

    @Override
    public Map<String, Object> getDispatchStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Hotspot> hotspots = hotspotMapper.findActive();
        List<Scooter> allScooters = scooterMapper.findAll();
        
        stats.put("totalHotspots", hotspots.size());
        stats.put("totalScooters", allScooters.size());
        stats.put("hotspots", hotspots);
        
        // 统计每个区域的滑板车数量
        List<Map<String, Object>> hotspotStats = new ArrayList<>();
        for (Hotspot h : hotspots) {
            Map<String, Object> hs = new HashMap<>();
            hs.put("id", h.getId());
            hs.put("name", h.getName());
            hs.put("weight", h.getWeight());
            hs.put("location", h.getLocation());
            hotspotStats.add(hs);
        }
        stats.put("hotspotStats", hotspotStats);
        
        return stats;
    }
}
