package com.zemcho.guzhe.mapper.screen;

import com.zemcho.guzhe.controller.screen_order.param.ScreenOrderManageListParam;
import com.zemcho.guzhe.controller.screen_order.vo.ScreenOrderManageInfoVo;
import com.zemcho.guzhe.controller.screen_order.vo.ScreenOrderManageItemVo;
import com.zemcho.guzhe.controller.screen_order.vo.ScreenOrderManageSummaryVo;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderListParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.vo.ScreenOrderInfoVo;
import com.zemcho.guzhe.controller.wechat.screenOrder.vo.ScreenOrderListItemVo;
import com.zemcho.guzhe.controller.wechat.screenOrder.vo.ScreenOrderSummaryVo;
import com.zemcho.guzhe.entity.screen.ScreenRentalOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ScreenRentalOrderMapper {
    /**
     * 新增租用订单
     *
     * @param data
     * @return
     */
    Integer insert(@Param("data") ScreenRentalOrder data);

    /**
     * 获取设备下已生效的商家id
     *
     * @param equipmentId
     * @return
     */
    List<Integer> selectShopIdByEquipmentId(@Param("equipmentId") Integer equipmentId);

    /**
     * 商家端店位订单汇总
     *
     * @param param
     * @param adminId
     * @param startTime
     * @param endTime
     * @return
     */
    ScreenOrderSummaryVo selectWechatOrderSummary(@Param("param") ScreenOrderListParam param,
                                                  @Param("adminId") Integer adminId,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 商家端店位订单列表
     *
     * @param param
     * @param adminId
     * @param startTime
     * @param endTime
     * @return
     */
    List<ScreenOrderListItemVo> selectWechatOrderLists(@Param("param") ScreenOrderListParam param,
                                                       @Param("adminId") Integer adminId,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime);

    /**
     * 商家端店位订单详情
     *
     * @param orderId
     * @param adminId
     * @return
     */
    ScreenOrderInfoVo selectWechatOrderInfo(@Param("orderId") Long orderId, @Param("adminId") Integer adminId);

    /**
     * 后台店位订单汇总
     *
     * @param param
     * @return
     */
    ScreenOrderManageSummaryVo selectAdminOrderSummary(@Param("param") ScreenOrderManageListParam param);

    /**
     * 后台店位订单列表
     *
     * @param param
     * @return
     */
    List<ScreenOrderManageItemVo> selectAdminOrderLists(@Param("param") ScreenOrderManageListParam param);

    /**
     * 根据ID查询订单
     *
     * @param id
     * @return
     */
    ScreenRentalOrder selectById(@Param("id") Long id);

    /**
     * 更新订单
     *
     * @param data
     * @return
     */
    Integer update(@Param("data") ScreenRentalOrder data);

    /**
     * 后台店位订单详情
     *
     * @param orderId
     * @return
     */
    ScreenOrderManageInfoVo selectAdminOrderInfo(@Param("orderId") Long orderId);
}
