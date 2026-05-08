package com.zemcho.guzhe.mapper.screen;

import com.zemcho.guzhe.entity.screen.ScreenRentalOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}
