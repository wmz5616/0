package com.zemcho.ddql.mapper.recharge;


import com.zemcho.ddql.entity.recharge.RechargeConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * 充值配置 Mapper 接口
 *
 * @author Ryan
 */
@Mapper
public interface RechargeConfigMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(@Param("data") RechargeConfig data);

    RechargeConfig selectById(@Param("id") Integer id);

    RechargeConfig select();

    int update(@Param("data") RechargeConfig data);
}
