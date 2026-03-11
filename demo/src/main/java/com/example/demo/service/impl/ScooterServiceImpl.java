package com.example.demo.service.impl;

import com.example.demo.entity.Scooter;
import com.example.demo.mapper.ScooterMapper;
import com.example.demo.service.ScooterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 滑板车服务实现类
 * 实现滑板车相关的业务逻辑
 */
@Service
public class ScooterServiceImpl implements ScooterService {

    @Autowired
    private ScooterMapper scooterMapper;

    /**
     * 获取所有滑板车
     */
    @Override
    public List<Scooter> findAll() {
        return scooterMapper.findAll();
    }

    /**
     * 根据ID查询滑板车
     */
    @Override
    public Scooter findById(Long id) {
        return scooterMapper.findById(id);
    }

    /**
     * 获取所有可用的滑板车（状态为AVAILABLE）
     */
    @Override
    public List<Scooter> findAvailable() {
        return scooterMapper.findAvailable();
    }

    /**
     * 添加滑板车
     */
    @Override
    public boolean save(Scooter scooter) {
        return scooterMapper.insert(scooter) > 0;
    }

    /**
     * 更新滑板车信息
     */
    @Override
    public boolean update(Scooter scooter) {
        return scooterMapper.update(scooter) > 0;
    }

    /**
     * 删除滑板车
     */
    @Override
    public boolean deleteById(Long id) {
        return scooterMapper.deleteById(id) > 0;
    }
}
