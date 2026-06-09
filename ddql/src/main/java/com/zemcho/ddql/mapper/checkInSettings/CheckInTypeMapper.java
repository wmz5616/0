package com.zemcho.ddql.mapper.checkInSettings;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.checkInSettings.vo.CheckInTypeVo;
import com.zemcho.ddql.entity.checkInSettings.CheckInType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CheckInTypeMapper {

    @Select("select count(*) from check_in_type where id = #{id}")
    Boolean ifExistById(@Param("id") Integer id);

    int insert(@Param("data") CheckInType checkInType);

    int update(@Param("data") CheckInType checkInType);

    List<CheckInTypeVo> selectList(@Param("param") SearchParam param);

    int delete(@Param("deleteIds") List<Integer> deleteIds);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    CheckInType selectById(@Param("id") Integer id);

    /**
     * 查询最大排序值
     *
     * @return
     */
    Integer selectMaxSort();
}
