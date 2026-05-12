package com.example.demo.mapper;

import com.example.demo.entity.Hotspot;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface HotspotMapper {
    @Select("SELECT * FROM hotspot")
    List<Hotspot> findAll();

    @Select("SELECT * FROM hotspot WHERE status = 'ACTIVE'")
    List<Hotspot> findActive();

    @Select("SELECT * FROM hotspot WHERE id = #{id}")
    Hotspot findById(Long id);

    @Insert("INSERT INTO hotspot(name, latitude, longitude, radius, weight, status, location) " +
            "VALUES(#{name}, #{latitude}, #{longitude}, #{radius}, #{weight}, #{status}, #{location})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Hotspot hotspot);

    @Update("UPDATE hotspot SET name=#{name}, latitude=#{latitude}, longitude=#{longitude}, " +
            "radius=#{radius}, weight=#{weight}, status=#{status}, location=#{location} WHERE id=#{id}")
    int update(Hotspot hotspot);

    @Update("UPDATE hotspot SET weight=#{weight} WHERE id=#{id}")
    int updateWeight(@Param("id") Long id, @Param("weight") Integer weight);

    @Update("UPDATE hotspot SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Delete("DELETE FROM hotspot WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT SUM(weight) FROM hotspot WHERE status = 'ACTIVE'")
    Integer sumActiveWeight();
}
