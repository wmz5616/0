package com.zemcho.guzhe.mapper.equipment;

import com.zemcho.guzhe.entity.equipment.EquipmentScreenshot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EquipmentScreenshotMapper {
    Integer insert(@Param("data") EquipmentScreenshot data);

    EquipmentScreenshot selectById(@Param("id") Long id);

    EquipmentScreenshot selectLatestByEquipmentId(@Param("equipmentId") Integer equipmentId);

    EquipmentScreenshot selectLatestSuccessByEquipmentId(@Param("equipmentId") Integer equipmentId);

    Integer update(@Param("data") EquipmentScreenshot data);
}
