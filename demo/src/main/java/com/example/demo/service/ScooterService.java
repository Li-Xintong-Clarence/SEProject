package com.example.demo.service;

import com.example.demo.entity.Scooter;
import java.util.List;

public interface ScooterService {
    List<Scooter> findAll();
    Scooter findById(Long id);
    List<Scooter> findAvailable();
    boolean save(Scooter scooter);
    boolean update(Scooter scooter);
    boolean deleteById(Long id);
}
