package com.example.demo.mapper;

import com.example.demo.entity.Card;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 支付卡Mapper接口
 * 对应数据库card表，使用MyBatis注解方式执行SQL
 */
@Mapper
public interface CardMapper {
    /**
     * 查询用户的支付卡列表
     */
    @Select("SELECT * FROM card WHERE user_id = #{userId}")
    List<Card> findByUserId(Long userId);

    /**
     * 根据ID查询支付卡
     */
    @Select("SELECT * FROM card WHERE id = #{id}")
    Card findById(Long id);

    /**
     * 插入新支付卡
     * useGeneratedKeys: 自动生成主键
     * keyProperty: 将生成的主键赋值给card对象的id属性
     */
    @Insert("INSERT INTO card (user_id, card_number, card_holder, expiry_date, cvv, is_default, created_at) " +
            "VALUES (#{userId}, #{cardNumber}, #{cardHolder}, #{expiryDate}, #{cvv}, #{isDefault}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Card card);

    /**
     * 更新支付卡信息
     */
    @Update("UPDATE card SET card_number=#{cardNumber}, card_holder=#{cardHolder}, " +
            "expiry_date=#{expiryDate}, cvv=#{cvv}, is_default=#{isDefault} WHERE id=#{id}")
    int update(Card card);

    /**
     * 删除支付卡
     */
    @Delete("DELETE FROM card WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 清除用户的所有默认卡
     * 将用户的is_default设为0
     */
    @Update("UPDATE card SET is_default = 0 WHERE user_id = #{userId}")
    int clearDefaultByUserId(Long userId);
}
