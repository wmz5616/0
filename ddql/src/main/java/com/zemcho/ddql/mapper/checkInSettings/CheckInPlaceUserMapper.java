package com.zemcho.ddql.mapper.checkInSettings;

import com.zemcho.ddql.entity.checkInSettings.CheckInPlaceUser;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CheckInPlaceUserMapper {

    int insert(@Param("data") List<CheckInPlaceUser> data);

    List<CheckInPlaceUser> select(@Param("placeId") Integer placeId, @Param("userId") Integer userId);

    @Delete("delete from check_in_place_user where place_id = #{placeId}")
    int deleteByPlaceId(@Param("placeId") Integer placeId);
}
