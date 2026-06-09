package com.zemcho.guzhe.mapper.order;

import com.zemcho.guzhe.controller.wechat.user.vo.ProductOrderAddressVo;
import com.zemcho.guzhe.entity.order.ProductOrderAddress;
import org.apache.ibatis.annotations.Param;

public interface ProductOrderAddressMapper {
    /**
     * 新增数据
     *
     * @param data
     * @return
     */
    Integer insert(@Param("data") ProductOrderAddress data);

    /**
     * 修改数据
     *
     * @param data
     * @return
     */
    Integer update(@Param("data") ProductOrderAddress data);

    /**
     * 根据订单ID查询
     *
     * @param orderId
     * @return
     */
    ProductOrderAddressVo selectByOrderId(@Param("orderId") Integer orderId);
}
