package com.example.demo.mapper;

import com.example.demo.entity.Card;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 支付卡Mapper接口
 * 对应数据库card表，使用MyBatis注解方式执行SQL
 *
 * 【安全说明】
 * 只操作脱敏后的卡片信息，不涉及完整卡号
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
     * 【安全】只存储后4位卡号和卡片类型，不存储完整卡号和CVV
     *
     * @param card 卡片对象，应只包含脱敏信息
     */
    @Insert("INSERT INTO card (user_id, card_holder, last_four, card_type, expiry_date, is_default, created_at) " +
            "VALUES (#{userId}, #{cardHolder}, #{lastFour}, #{cardType}, #{expiryDate}, #{isDefault}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Card card);

    /**
     * 更新支付卡信息
     * 【安全】只更新脱敏后的信息
     */
    @Update("UPDATE card SET card_holder=#{cardHolder}, last_four=#{lastFour}, " +
            "card_type=#{cardType}, expiry_date=#{expiryDate}, is_default=#{isDefault} WHERE id=#{id}")
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
