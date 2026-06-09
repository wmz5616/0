package com.zemcho.guzhe.mapper.order;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.data.vo.BusinessEquipmentVO;
import com.zemcho.guzhe.controller.wechat.shop.vo.BusinessDataVO;
import com.zemcho.guzhe.entity.order.Order;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 查询列表
     *
     * @param param
     * @return
     */
    List<Order> selectLists(@Param("param") SearchParam param);

    BusinessDataVO selectBusinessData(@Param("param") SearchParam param);

    BusinessDataVO selectMonitorOrderData(@Param("startTime") LocalDateTime startTime,@Param("endTime") LocalDateTime endTime);

    Integer selectCompletedOrderCount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    List<BusinessEquipmentVO> selectBusinessEquipment(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    List<Order> selectOrderListForReconciliation(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    BusinessDataVO selectSystemOrderData(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
