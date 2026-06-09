package com.zemcho.guzhe.mapper.screen;

import com.zemcho.guzhe.entity.screen.ScreenRentalDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScreenRentalDetailMapper {
    /**
     * 批量新增租用明细
     *
     * @param list
     * @return
     */
    Integer insertBatch(@Param("list") List<ScreenRentalDetail> list);

    /**
     * 查询商超下已被占用的月份明细
     *
     * @param businessCircleId
     * @param monthKeys
     * @return
     */
    List<ScreenRentalDetail> selectActiveByBusinessCircleIdAndMonthKeys(@Param("businessCircleId") Integer businessCircleId,
                                                                        @Param("monthKeys") List<String> monthKeys);

    /**
     * 查询设备在指定月份下的占用明细
     *
     * @param equipmentId
     * @param monthKeys
     * @return
     */
    List<ScreenRentalDetail> selectActiveByEquipmentAndMonthKeys(@Param("equipmentId") Integer equipmentId,
                                                                 @Param("monthKeys") List<String> monthKeys);

    /**
     * 查询多个设备在指定月份下的占用明细
     *
     * @param equipmentIds
     * @param monthKeys
     * @return
     */
    List<ScreenRentalDetail> selectActiveByEquipmentIdsAndMonthKeys(@Param("equipmentIds") List<Integer> equipmentIds,
                                                                     @Param("monthKeys") List<String> monthKeys);

    /**
     * 按订单更新明细状态
     *
     * @param orderId
     * @param status
     * @return
     */
    Integer updateStatusByOrderId(@Param("orderId") Long orderId, @Param("status") Integer status);

    /**
     * 按当前年月刷新所有已审核订单明细状态
     *
     * @param currentYear  当前年
     * @param currentMonth 当前月
     * @return
     */
    Integer refreshApprovedStatus(@Param("currentYear") Integer currentYear, @Param("currentMonth") Integer currentMonth);

    /**
     * 按当前年月刷新指定订单明细状态
     *
     * @param orderId      订单ID
     * @param currentYear  当前年
     * @param currentMonth 当前月
     * @return
     */
    Integer refreshApprovedStatusByOrderId(@Param("orderId") Long orderId,
                                           @Param("currentYear") Integer currentYear,
                                           @Param("currentMonth") Integer currentMonth);
}
