package com.example.demo.mapper;

import com.example.demo.entity.Depot;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface DepotMapper {
    @Select("SELECT * FROM depot WHERE status = 'ACTIVE'")
    List<Depot> findAllActive();

    @Select("SELECT * FROM depot")
    List<Depot> findAll();

    @Select("SELECT *, (SELECT COUNT(*) FROM scooters WHERE depot_id = depot.id AND status != 'RETIRED') as current_stock FROM depot")
    List<Depot> findAllWithStock();

    @Select("SELECT * FROM depot WHERE id = #{id}")
    Depot findById(Long id);

    @Select("SELECT * FROM depot WHERE depot_number = #{depotNumber}")
    Depot findByNumber(String depotNumber);

    @Insert("INSERT INTO depot(depot_number, name, latitude, longitude, address, capacity, status) " +
            "VALUES(#{depotNumber}, #{name}, #{latitude}, #{longitude}, #{address}, #{capacity}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Depot depot);

    @Update("UPDATE depot SET " +
            "depot_number = #{depotNumber}, " +
            "name = #{name}, " +
            "latitude = #{latitude}, " +
            "longitude = #{longitude}, " +
            "address = #{address}, " +
            "capacity = #{capacity}, " +
            "status = #{status} " +
            "WHERE id = #{id}")
    int update(Depot depot);

    @Delete("DELETE FROM depot WHERE id = #{id}")
    int deleteById(Long id);

    // ============ 统计查询 ============

    @Select("SELECT COUNT(*) FROM depot WHERE status = 'ACTIVE'")
    int countDepots();
}
