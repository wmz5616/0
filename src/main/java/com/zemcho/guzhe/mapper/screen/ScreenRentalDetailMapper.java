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
}
