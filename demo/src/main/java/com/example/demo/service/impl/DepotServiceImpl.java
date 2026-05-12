package com.example.demo.service.impl;

import com.example.demo.entity.Depot;
import com.example.demo.entity.Scooter;
import com.example.demo.mapper.DepotMapper;
import com.example.demo.mapper.ScooterMapper;
import com.example.demo.service.DepotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务点服务实现类
 */
@Service
public class DepotServiceImpl implements DepotService {

    @Autowired
    private DepotMapper depotMapper;

    @Autowired
    private ScooterMapper scooterMapper;

    @Override
    public List<Depot> findAllActive() {
        return depotMapper.findAllActive();
    }

    @Override
    public List<Depot> findAll() {
        return depotMapper.findAll();
    }

    @Override
    public Depot findById(Long id) {
        return depotMapper.findById(id);
    }

    @Override
    public Depot findByNumber(String depotNumber) {
        return depotMapper.findByNumber(depotNumber);
    }

    @Override
    public boolean save(Depot depot) {
        return depotMapper.insert(depot) > 0;
    }

    @Override
    public boolean update(Depot depot) {
        return depotMapper.update(depot) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return depotMapper.deleteById(id) > 0;
    }

    @Override
    public Map<String, Object> getDepotWithScooterCount(Long depotId) {
        Depot depot = depotMapper.findById(depotId);
        if (depot == null) return null;

        Map<String, Object> result = new HashMap<>();
        result.put("depot", depot);

        List<Scooter> allScooters = scooterMapper.findAll();
        List<Scooter> depotScooters = allScooters.stream()
                .filter(s -> depotId.equals(s.getDepotId()))
                .collect(Collectors.toList());

        long availableCount = depotScooters.stream()
                .filter(s -> "AVAILABLE".equals(s.getStatus()))
                .count();
        long inUseCount = depotScooters.stream()
                .filter(s -> "IN_USE".equals(s.getStatus()))
                .count();

        result.put("totalScooters", depotScooters.size());
        result.put("availableCount", availableCount);
        result.put("inUseCount", inUseCount);

        return result;
    }

    @Override
    public List<Map<String, Object>> getAllDepotsWithScooterCount() {
        List<Depot> depots = depotMapper.findAllActive();
        List<Scooter> allScooters = scooterMapper.findAll();

        return depots.stream().map(depot -> {
            Map<String, Object> result = new HashMap<>();
            result.put("id", depot.getId());
            result.put("depotNumber", depot.getDepotNumber());
            result.put("name", depot.getName());
            result.put("latitude", depot.getLatitude());
            result.put("longitude", depot.getLongitude());
            result.put("address", depot.getAddress());
            result.put("capacity", depot.getCapacity());
            result.put("status", depot.getStatus());

            List<Scooter> depotScooters = allScooters.stream()
                    .filter(s -> depot.getId().equals(s.getDepotId()))
                    .collect(Collectors.toList());

            long availableCount = depotScooters.stream()
                    .filter(s -> "AVAILABLE".equals(s.getStatus()))
                    .count();
            long inUseCount = depotScooters.stream()
                    .filter(s -> "IN_USE".equals(s.getStatus()))
                    .count();
            long currentStock = depotScooters.size();

            result.put("totalScooters", currentStock);
            result.put("availableCount", availableCount);
            result.put("inUseCount", inUseCount);
            result.put("currentStock", currentStock);

            return result;
        }).collect(Collectors.toList());
    }
}
