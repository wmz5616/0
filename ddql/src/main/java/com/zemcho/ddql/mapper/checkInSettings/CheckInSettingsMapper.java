package com.zemcho.ddql.mapper.checkInSettings;

import com.zemcho.ddql.entity.checkInSettings.CheckInSettings;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CheckInSettingsMapper {

    @Select("select * from check_in_settings limit 1")
    CheckInSettings get();

    int update(@Param("data") CheckInSettings checkInSettings);
}
