package com.zemcho.ddql.mapper.equipment;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.equipment.vo.EquipmentVo;
import com.zemcho.ddql.entity.equipment.Equipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Mapper
public interface EquipmentMapper {

    @Select("select count(*) from equipment where id = #{id} and delete_time is null")
    Boolean ifExistsById(@Param("id") Integer id);

    @Select("select count(*) from equipment where serial_number = #{serialNumber} and id != #{id} and delete_time is " +
            "null")
    Boolean ifExistsBySerialNumber(@Param("serialNumber") String serialNumber, @Param("id") Integer id);

    int insert(@Param("data") Equipment data);

    int update(@Param("data") Equipment data);

    // keyword(序列号serialNumber) searchIntStatus(在线状态onlineStatus) searchType(打卡类型id checkInTypeId) searchStrField1
    // (打卡场所名称name) searchField1(启用状态enableStatus) startTime(createTime) endTime(createTime)
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

    /**
     * 根据打卡场所id查询设备信息
     *
     * @param placeId
     * @return
     */
    Equipment selectByPlaceId(@Param("placeId") Integer placeId);

    /**
     * 根据打卡场所id批量查询设备信息
     *
     * @param placeIds
     * @return
     */
    List<Equipment> selectByPlaceIds(@Param("placeIds") Collection<Integer> placeIds);
}
