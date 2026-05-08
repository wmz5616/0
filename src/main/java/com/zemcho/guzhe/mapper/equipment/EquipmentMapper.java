package com.zemcho.guzhe.mapper.equipment;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.equipment.param.EquipmentParam;
import com.zemcho.guzhe.controller.equipment.vo.EquipmentVo;
import com.zemcho.guzhe.entity.equipment.Equipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Mapper
public interface EquipmentMapper {

    @Select("select count(*) from equipment where serial_number = #{id} and delete_time is null")
    Boolean ifExistsById(@Param("id") String id);

    @Select("select count(*) from equipment where serial_number = #{serialNumber} and id != #{id} and delete_time is " +
            "null")
    Boolean ifExistsBySerialNumber(@Param("serialNumber") String serialNumber, @Param("id") Integer id);

    int insert(@Param("data") EquipmentParam data);

    int update(@Param("data") EquipmentParam data);

    List<EquipmentVo> select(@Param("param") SearchParam param);

    int delete(@Param("deleteIds") List<Integer> deleteIds);

    /**
     * 根据序列号查询设备信息
     *
     * @param serialNumber
     * @return
     */
    Equipment selectBySerialNumber(@Param("serialNumber") String serialNumber);

    /**
     * 根据id查询设备信息
     *
     * @param id
     * @return
     */
    Equipment selectById(@Param("id") Integer id);

    /**
     * 获取离线设备数据
     *
     * @param checkTime
     * @return
     */
    List<Equipment> selectOffList(@Param("checkTime") LocalDateTime checkTime);

    /**
     * 批量更新状态
     *
     * @param ids
     * @param status
     * @return
     */
    Integer updateStatusByIds(@Param("ids") Collection<Integer> ids, @Param("status") Integer status);

    int updateEquipmentStatus(@Param("data") Equipment equipment);

    /**
     * 编辑设备信息
     *
     * @param data
     * @return
     */
    int updateByEquipment(@Param("data") Equipment data);
}
