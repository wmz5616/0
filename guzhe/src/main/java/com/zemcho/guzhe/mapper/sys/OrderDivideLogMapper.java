package com.zemcho.guzhe.mapper.sys;

import com.zemcho.guzhe.entity.sys.OrderDivideLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单分账记录 Mapper
 */
@Mapper
public interface OrderDivideLogMapper {
    int insert(@Param("data") OrderDivideLog data);
}
