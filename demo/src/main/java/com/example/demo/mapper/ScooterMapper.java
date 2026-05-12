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

    @Select("SELECT * FROM scooters WHERE scooter_number = #{scooterNumber}")
    Scooter findByScooterNumber(String scooterNumber);

    @Select("SELECT * FROM scooters WHERE status = 'AVAILABLE'")
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
            "UPDATE scooters SET " +
            "<if test='scooterNumber != null'>scooter_number=#{scooterNumber},</if>" +
            "<if test='status != null'>status=#{status},</if>" +
            "<if test='batteryLevel != null'>battery_level=#{batteryLevel},</if>" +
            "<if test='latitude != null'>latitude=#{latitude},</if>" +
            "<if test='longitude != null'>longitude=#{longitude},</if>" +
            "<if test='location != null'>location=#{location},</if>" +
            "<if test='depotId != null'>depot_id=#{depotId},</if>" +
            "<if test='lastMaintenanceDate != null'>last_maintenance_date=#{lastMaintenanceDate}</if>" +
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
