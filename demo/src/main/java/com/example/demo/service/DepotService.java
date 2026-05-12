package com.example.demo.service;

import com.example.demo.entity.Depot;
import java.util.List;
import java.util.Map;

/**
 * 服务点服务接口
 */
public interface DepotService {
    List<Depot> findAllActive();
    List<Depot> findAll();
    Depot findById(Long id);
    Depot findByNumber(String depotNumber);
    boolean save(Depot depot);
    boolean update(Depot depot);
    boolean deleteById(Long id);
    Map<String, Object> getDepotWithScooterCount(Long depotId);
    List<Map<String, Object>> getAllDepotsWithScooterCount();
}
