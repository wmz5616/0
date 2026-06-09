package com.zemcho.ddql.mapper.order;

import com.zemcho.ddql.entity.order.ExchangeOrderLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExchangeOrderLogMapper {
    /**
     * 新增数据
     *
     * @param data
     * @return
     */
    Integer insert(@Param("data") ExchangeOrderLog data);

    /**
     * 根据订单ID查询
     *
     * @param orderId
     * @return
     */
    List<ExchangeOrderLog> selectByOrderId(Integer orderId);
}
