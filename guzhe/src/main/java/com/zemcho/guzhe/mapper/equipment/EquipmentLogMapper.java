package com.zemcho.guzhe.mapper.equipment;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.entity.equipment.Equipment;
import com.zemcho.guzhe.entity.equipment.EquipmentLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface EquipmentLogMapper {
    /**
     * 新增设备日志
     *
     * @param data
     * @return
     */
    int insert(@Param("data") EquipmentLog data);

    /**
     * 批量新增设备日志
     *
     * @param data
     * @return
     */
    int insertAll(@Param("data") Collection<EquipmentLog> data);

    /**
     * 查询列表
     *
     * @param param
     * @return
     */
    List<EquipmentLog> selectLists(@Param("param") SearchParam param);


}
