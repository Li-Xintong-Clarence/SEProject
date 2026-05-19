package com.example.demo.mapper;

import com.example.demo.entity.Scooter;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ScooterMapper {
    @Select("SELECT s.id, s.scooter_number, s.status, s.battery_level, s.latitude, s.longitude, " +
            "COALESCE(s.location, d.name) as location, s.depot_id, d.name as depot_name, s.last_maintenance_date " +
            "FROM scooters s LEFT JOIN depot d ON s.depot_id = d.id")
    List<Scooter> findAll();

    @Select("SELECT s.id, s.scooter_number, s.status, s.battery_level, s.latitude, s.longitude, " +
            "COALESCE(s.location, d.name) as location, s.depot_id, d.name as depot_name, s.last_maintenance_date " +
            "FROM scooters s LEFT JOIN depot d ON s.depot_id = d.id WHERE s.id = #{id}")
    Scooter findById(Long id);

    @Select("SELECT s.id, s.scooter_number, s.status, s.battery_level, s.latitude, s.longitude, " +
            "COALESCE(s.location, d.name) as location, s.depot_id, d.name as depot_name, s.last_maintenance_date " +
            "FROM scooters s LEFT JOIN depot d ON s.depot_id = d.id WHERE s.scooter_number = #{scooterNumber}")
    Scooter findByScooterNumber(String scooterNumber);

    @Select("SELECT s.id, s.scooter_number, s.status, s.battery_level, s.latitude, s.longitude, " +
            "COALESCE(s.location, d.name) as location, s.depot_id, d.name as depot_name, s.last_maintenance_date " +
            "FROM scooters s LEFT JOIN depot d ON s.depot_id = d.id WHERE s.status = 'AVAILABLE'")
    List<Scooter> findAvailable();

    @Select("SELECT * FROM scooters WHERE depot_id = #{depotId}")
    List<Scooter> findByDepotId(Long depotId);

    @Select("SELECT * FROM scooters WHERE depot_id = #{depotId} AND status = 'AVAILABLE'")
    List<Scooter> findAvailableByDepotId(Long depotId);

    @Insert("INSERT INTO scooters(scooter_number, status, battery_level, latitude, longitude, location, depot_id, last_maintenance_date) " +
            "VALUES(#{scooterNumber}, #{status}, #{batteryLevel}, #{latitude}, #{longitude}, #{location}, #{depotId}, #{lastMaintenanceDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Scooter scooter);

    @Update("<script>" +
            "UPDATE scooters " +
            "<set>" +
            "<if test='scooterNumber != null'>scooter_number=#{scooterNumber},</if>" +
            "<if test='status != null'>status=#{status},</if>" +
            "<if test='batteryLevel != null'>battery_level=#{batteryLevel},</if>" +
            "<if test='latitude != null'>latitude=#{latitude},</if>" +
            "<if test='longitude != null'>longitude=#{longitude},</if>" +
            "<if test='location != null'>location=#{location},</if>" +
            "<if test='depotId != null'>depot_id=#{depotId},</if>" +
            "<if test='lastMaintenanceDate != null'>last_maintenance_date=#{lastMaintenanceDate},</if>" +
            "</set>" +
            " WHERE id=#{id}" +
            "</script>")
    int update(Scooter scooter);

    @Update("UPDATE scooters SET status=#{status}, depot_id=#{depotId} WHERE id=#{id}")
    int updateStatusAndDepot(@Param("id") Long id, @Param("status") String status, @Param("depotId") Long depotId);

    @Update("UPDATE scooters SET status=#{status} WHERE id=#{id}")
    int updateStatus(Long id, String status);

    @Delete("DELETE FROM scooters WHERE id = #{id}")
    int deleteById(Long id);

    // ============ 统计查询 ============

    @Select("SELECT COUNT(*) FROM scooters WHERE status != 'RETIRED'")
    int countScooters();

    @Select("SELECT COUNT(*) FROM scooters WHERE status = #{status}")
    int countByStatus(String status);

    @Select("SELECT COUNT(*) FROM scooters WHERE battery_level < 20 AND status != 'RETIRED'")
    int countLowBattery();
}
