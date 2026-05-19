package com.example.demo.mapper;

import com.example.demo.entity.OvertimeFee;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 超时费用配置Mapper接口
 */
@Mapper
public interface OvertimeFeeMapper {
    @Select("SELECT * FROM overtime_fee ORDER BY hire_option")
    List<OvertimeFee> findAll();

    @Select("SELECT * FROM overtime_fee WHERE id = #{id}")
    OvertimeFee findById(Long id);

    @Select("SELECT * FROM overtime_fee WHERE hire_option = #{hireOption}")
    OvertimeFee findByHireOption(String hireOption);

    @Insert("INSERT INTO overtime_fee(hire_option, hire_option_name, fee_type, fee, max_overtime_minutes, enabled) " +
            "VALUES(#{hireOption}, #{hireOptionName}, #{feeType}, #{fee}, #{maxOvertimeMinutes}, #{enabled})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OvertimeFee overtimeFee);

    @Update("UPDATE overtime_fee SET hire_option_name=#{hireOptionName}, fee_type=#{feeType}, " +
            "fee=#{fee}, max_overtime_minutes=#{maxOvertimeMinutes}, enabled=#{enabled} WHERE id=#{id}")
    int update(OvertimeFee overtimeFee);

    @Delete("DELETE FROM overtime_fee WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) FROM overtime_fee WHERE hire_option = #{hireOption}")
    int countByHireOption(String hireOption);
}
