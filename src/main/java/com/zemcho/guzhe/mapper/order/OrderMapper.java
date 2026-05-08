package com.zemcho.guzhe.mapper.order;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.wechat.shop.vo.BusinessDataVO;
import com.zemcho.guzhe.entity.order.Order;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

public interface OrderMapper {
    /**
     * 新增数据
     *
     * @param data
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insert(@Param("data") Order data);

    /**
     * 更新数据
     *
     * @param data
     * @return
     */
    Integer update(@Param("data") Order data);

    /**
     * 根据订单id更新数据
     *
     * @param data
     * @return
     */
    Integer updateByOrderId(@Param("data") Order data);

    /**
     * 根据订单id查询
     *
     * @param orderType
     * @param orderId
     * @return
     */
    Order selectByOrderId(@Param("orderType") Integer orderType, @Param("orderId") Integer orderId);

    BusinessDataVO selectBusinessData(@Param("param") SearchParam param);
}
