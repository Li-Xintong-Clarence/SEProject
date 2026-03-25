package com.example.demo.service;

import com.example.demo.entity.Scooter;
import java.util.List;

/**
 * 电动车服务接口
 * 定义电动车相关的业务操作
 */
public interface ScooterService {
    List<Scooter> findAll();
    Scooter findById(Long id);
    List<Scooter> findAvailable();
    boolean save(Scooter scooter);
    boolean update(Scooter scooter);
    boolean deleteById(Long id);
    boolean updateStatus(Long id, String status);
}
