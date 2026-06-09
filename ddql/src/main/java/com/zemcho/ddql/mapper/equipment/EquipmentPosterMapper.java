package com.zemcho.ddql.mapper.equipment;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.equipment.EquipmentPoster;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@Mapper
public interface EquipmentPosterMapper {

    int insertBatch(@Param("data") List<EquipmentPoster> data);

    int updateBatch(@Param("list") List<EquipmentPoster> list);

    @Delete("delete from equipment_poster where equipment_id = #{equipmentId}")
    int deleteByEquipmentId(@Param("equipmentId") Integer equipmentId);

    @Select("select * from equipment_poster where equipment_id = #{equipmentId} order by sort")
    List<EquipmentPoster> selectByEquipmentId(@Param("equipmentId") Integer equipmentId);

    @Select("select * from equipment_poster where status = #{status}")
    List<EquipmentPoster> selectByStatus(@Param("status") Integer status);

    /**
     * 查询设备海报列表
     *
     * @param param
     * @return
     */
    List<EquipmentPoster> selectLists(@Param("param") SearchParam param);

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    Integer deleteByIds(@Param("ids") Collection<Integer> ids);
}
