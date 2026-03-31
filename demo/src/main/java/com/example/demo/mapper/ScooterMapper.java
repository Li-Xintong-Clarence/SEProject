package com.example.demo.mapper;

import com.example.demo.entity.Scooter;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ScooterMapper {
    @Select("SELECT * FROM scooters")
    List<Scooter> findAll();

    @Select("SELECT * FROM scooters WHERE id = #{id}")
    Scooter findById(Long id);

    @Select("SELECT * FROM scooters WHERE status = 'AVAILABLE'")
    List<Scooter> findAvailable();

    @Insert("INSERT INTO scooters(scooter_number, status, battery_level, latitude, longitude, location, last_maintenance_date) " +
            "VALUES(#{scooterNumber}, #{status}, #{batteryLevel}, #{latitude}, #{longitude}, #{location}, #{lastMaintenanceDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Scooter scooter);

    int update(Scooter scooter);

    @Update("UPDATE scooters SET status=#{status} WHERE id=#{id}")
    int updateStatus(Long id, String status);

    @Delete("DELETE FROM scooters WHERE id = #{id}")
    int deleteById(Long id);
}
