package com.zemcho.ddql.mapper.order;

import com.zemcho.ddql.controller.wechat.index.vo.ExchangeOrderAddressVo;
import com.zemcho.ddql.entity.order.ExchangeOrderAddress;
import org.apache.ibatis.annotations.Param;

public interface ExchangeOrderAddressMapper {
    /**
     * 新增数据
     *
     * @param data
     * @return
     */
    Integer insert(@Param("data") ExchangeOrderAddress data);

    /**
     * 根据订单ID查询
     *
     * @param orderId
     * @return
     */
    ExchangeOrderAddressVo selectByOrderId(Integer orderId);
}
