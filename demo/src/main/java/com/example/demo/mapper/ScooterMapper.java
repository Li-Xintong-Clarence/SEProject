package com.example.demo.mapper;

import com.example.demo.entity.Scooter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ScooterMapper {
    List<Scooter> findAll();
    Scooter findById(@Param("id") Long id);
    List<Scooter> findAvailable();
    int insert(Scooter scooter);
    int update(Scooter scooter);
    int deleteById(@Param("id") Long id);
}
