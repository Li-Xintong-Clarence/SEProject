package com.example.demo.mapper;

import com.example.demo.entity.Pricing;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 价格配置Mapper接口
 * 对应数据库pricing表，使用MyBatis注解方式执行SQL
 */
@Mapper
public interface PricingMapper {
    /**
     * 查询所有价格配置
     */
    @Select("SELECT * FROM pricing")
    List<Pricing> findAll();
    /**
     * 根据ID查询价格配置
     */
    @Select("SELECT * FROM pricing WHERE id = #{id}")
    Pricing findById(Long id);
    /**
     * 插入新价格配置
     */
    @Insert("INSERT INTO pricing(hire_option, price, description) VALUES(#{hireOption}, #{price}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Pricing pricing);
    /**
     * 更新价格配置
     */
    @Update("UPDATE pricing SET hire_option=#{hireOption}, price=#{price}, description=#{description} WHERE id=#{id}")
    int update(Pricing pricing);
    /**
     * 删除价格配置
     */
    @Delete("DELETE FROM pricing WHERE id = #{id}")
    int deleteById(Long id);
}
