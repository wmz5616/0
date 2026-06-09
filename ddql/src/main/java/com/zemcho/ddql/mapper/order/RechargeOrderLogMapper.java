package com.zemcho.ddql.mapper.order;

import com.zemcho.ddql.entity.order.RechargeOrderLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RechargeOrderLogMapper {
    /**
     * 新增数据
     *
     * @param data
     * @return
     */
    Integer insert(@Param("data") RechargeOrderLog data);

    /**
     * 根据订单ID查询
     *
     * @param orderId
     * @return
     */
    List<RechargeOrderLog> selectByOrderId(Integer orderId);
}
