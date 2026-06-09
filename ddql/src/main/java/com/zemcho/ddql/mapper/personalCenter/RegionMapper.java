package com.zemcho.ddql.mapper.personalCenter;

import com.zemcho.ddql.entity.personalCenter.Region;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RegionMapper {

    @Select("select count(*) from zc_region where id = #{id}")
    Boolean ifExistsById(@Param("id") Integer id);

    @Select("SELECT * FROM zc_region WHERE pid = #{pid}")
    List<Region> selectLowRegions(@Param("pid") Integer pid);

    Region selectRegionParent(@Param("id") Integer id);

    @Select("SELECT * FROM zc_region WHERE id = #{id}")
    Region selectById(@Param("id") Integer id);
}
