package com.zemcho.guzhe.mapper.log;

import com.zemcho.guzhe.entity.log.PayLog;
import org.apache.ibatis.annotations.Param;

public interface PayLogMapper {
    /**
     * 新增支付日志记录
     *
     * @param data
     * @return
     */
    Integer insert(@Param("data") PayLog data);

    /**
     * 修改支付日志记录
     *
     * @param data
     * @return
     */
    Integer update(@Param("data") PayLog data);

    /**
     * 根据订单id查询支付日志记录
     *
     * @param orderType
     * @param orderId
     * @param status
     * @return
     */
    PayLog selectByOrderId(@Param("orderType") Integer orderType, @Param("orderId") Integer orderId,
                           @Param("status") Integer status);
}
