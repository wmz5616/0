package com.zemcho.ddql.mapper.business;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.business.CoinRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CoinRuleMapper {
    
    /**
     * 根据ID查询
     */
    CoinRule selectById(@Param("id") Integer id);
    
    /**
     * 根据商家ID查询
     */
    CoinRule selectByShopId(@Param("shopId") Integer shopId);
    
    /**
     * 新增
     */
    int insert(@Param("param") CoinRule coinRule);
    
    /**
     * 更新
     */
    int update(@Param("param") CoinRule coinRule);
    
    /**
     * 根据ID删除
     */
    int deleteById(@Param("id") Integer id);

}