package com.zemcho.ddql.mapper.checkInSettings;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.checkInSettings.vo.CheckInPlaceVo;
import com.zemcho.ddql.entity.checkInSettings.CheckInPlace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@Mapper
public interface CheckInPlaceMapper {

    // 根据name判断是否存在
    @Select("select count(*) from check_in_place where name = #{name} and id != #{id} and delete_time is null")
    Boolean ifExistsByName(@Param("name") String name, @Param("id") Integer id);

    // 根据id判断是否存在
    @Select("select count(*) from check_in_place where id = #{id} and delete_time is null")
    Boolean ifExistsById(@Param("id") Integer id);

    int insert(@Param("data") CheckInPlace data);

    int update(@Param("data") CheckInPlace data);

    // name(keyword) checkInTypeId(searchId) status(searchIntStatus) startTime endTime pageNum pageSize
    List<CheckInPlaceVo> select(@Param("param") SearchParam param);

    int delete(@Param("deleteIds") List<Integer> deleteIds);

    List<CheckInPlaceVo> selectByIds(@Param("ids") List<Integer> ids);

    /**
     * 根据id查询打卡场所信息
     *
     * @param id
     * @return
     */
    CheckInPlace selectById(@Param("id") Integer id);

    /**
     * 批量更新状态
     *
     * @param ids
     * @param status
     * @return
     */
    Integer updateStatusByIds(@Param("ids") Collection<Integer> ids, @Param("status") Integer status);
}
