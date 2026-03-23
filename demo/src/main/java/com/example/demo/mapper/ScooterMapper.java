package com.example.demo.mapper;

import com.example.demo.entity.Scooter;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 电动车Mapper接口
 * 对应数据库scooters表，使用MyBatis注解方式执行SQL
 */
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

    @Update("UPDATE scooters SET scooter_number=#{scooterNumber}, status=#{status}, battery_level=#{batteryLevel}, " +
            "latitude=#{latitude}, longitude=#{longitude}, location=#{location}, last_maintenance_date=#{lastMaintenanceDate} WHERE id=#{id}")
    int update(Scooter scooter);

    @Delete("DELETE FROM scooters WHERE id = #{id}")
    int deleteById(Long id);

    @Update("UPDATE scooters SET status=#{status} WHERE id=#{id}")
    int updateStatus(Long id, String status);
}
