package com.example.demo.service.impl;

import com.example.demo.entity.Scooter;
import com.example.demo.mapper.ScooterMapper;
import com.example.demo.service.ScooterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScooterServiceImpl implements ScooterService {

    @Autowired
    private ScooterMapper scooterMapper;

    @Override
    public List<Scooter> findAll() {
        return scooterMapper.findAll();
    }

    @Override
    public Scooter findById(Long id) {
        return scooterMapper.findById(id);
    }

    @Override
    public List<Scooter> findAvailable() {
        return scooterMapper.findAvailable();
    }

    @Override
    public boolean save(Scooter scooter) {
        return scooterMapper.insert(scooter) > 0;
    }

    @Override
    public boolean update(Scooter scooter) {
        return scooterMapper.update(scooter) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return scooterMapper.deleteById(id) > 0;
    }
}
