package com.zemcho.guzhe.mapper.order;

import com.zemcho.guzhe.entity.order.ProductOrderLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductOrderLogMapper {
    /**
     * 新增数据
     *
     * @param data
     * @return
     */
    Integer insert(@Param("data") ProductOrderLog data);

    /**
     * 根据订单ID查询
     *
     * @param orderId
     * @return
     */
    List<ProductOrderLog> selectByOrderId(@Param("orderId") Integer orderId);
}
