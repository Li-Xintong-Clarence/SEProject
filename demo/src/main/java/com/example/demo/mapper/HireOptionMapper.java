package com.example.demo.mapper;

import com.example.demo.entity.HireOption;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租用选项Mapper接口
 * 定义租用选项相关的数据库操作方法（ID4：查看租用选项和费用）
 */
@Mapper
public interface HireOptionMapper {
    /**
     * 查询所有可用的租用选项
     * @return 租用选项列表
     */
    List<HireOption> findAllActive();

    /**
     * 更新租用选项
     * @param option 租用选项对象
     * @return 影响的行数
     */
    int update(HireOption option);
}
