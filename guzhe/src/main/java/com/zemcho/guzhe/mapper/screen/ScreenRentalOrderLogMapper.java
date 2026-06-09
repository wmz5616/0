package com.zemcho.guzhe.mapper.screen;

import com.zemcho.guzhe.entity.screen.ScreenRentalOrderLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScreenRentalOrderLogMapper {
    /**
     * 新增订单操作记录
     *
     * @param data
     * @return
     */
    Integer insert(@Param("data") ScreenRentalOrderLog data);

    /**
     * 查询订单操作记录
     *
     * @param orderId
     * @return
     */
    List<ScreenRentalOrderLog> selectByOrderId(@Param("orderId") Long orderId);
}
